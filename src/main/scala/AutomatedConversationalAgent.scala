import AkkaHttpServer.system
import akka.actor.ActorSystem
import io.github.ollama4j.OllamaAPI
import io.github.ollama4j.utils.Options
import org.slf4j.LoggerFactory
import protobuf.llmQuery.LlmQueryRequest

import scala.collection.mutable.ListBuffer
import scala.concurrent.Await
import scala.concurrent.duration._
import scala.jdk.CollectionConverters._

/**
 * AutomatedConversationalAgent orchestrates API invocations to process conversational inputs
 * and generate refined responses in multiple iterations.
 */
object AutomatedConversationalAgent {
  private val logger = LoggerFactory.getLogger(getClass)

  // Configuration keys for external APIs
  private val OLLAMA_HOST = "ollama.host"
  private val OLLAMA_REQUEST_TIMEOUT = "ollama.request-timeout-seconds"
  private val OLLAMA_MODEL = "ollama.model"
  private val OLLAMA_QUERIES_RANGE = "ollama.range"

  // Input prefixes for query generation
  private val LLAMA_PREFIX = "how can you respond to the statement "
  private val LLAMA_TO_LAMBDA_PREFIX = "Do you have any comments on "

  /**
   * Main entry point for the AutomatedConversationalAgent.
   *
   * @param args Array of arguments where the first argument is the seed text.
   */
  def main(args: Array[String]): Unit = {
    val seedText = args.headOption.getOrElse("What is cloud computing?")
    val protoRequest = new LlmQueryRequest(seedText, 100)
    implicit val system: ActorSystem = ActorSystem("ConversationAgent")

    try {
      start(protoRequest)
    } finally {
      system.terminate()
    }
  }

  /**
   * Starts the conversational processing loop by invoking APIs in sequence.
   *
   * @param protoRequest Initial LLM request object.
   * @param system       Implicit ActorSystem for managing API calls.
   */
  def start(protoRequest: LlmQueryRequest)(implicit system: ActorSystem): Unit = {
    val llamaAPI = initializeLlamaAPI()
    val llamaModel = ConfigLoader.getConfig(OLLAMA_MODEL)
    val iterations = ConfigLoader.getConfig(OLLAMA_QUERIES_RANGE).toInt

    val results = YAML_Helper.createMutableResult()
    var currentRequest = protoRequest

    // Sequentially process each iteration
    Iterator.range(0, iterations).foreach { iteration =>
      try {
        processIteration(iteration, currentRequest, llamaAPI, llamaModel, results) match {
          case Some(nextRequest) => currentRequest = nextRequest
          case None => logger.warn(s"Stopping early at iteration $iteration due to empty response.")
        }
      } catch {
        case e: Exception =>
          logger.error(s"Processing failed at iteration $iteration: ${e.getMessage}", e)
          throw e
      }
    }

    // Save results after all iterations
    YAML_Helper.save(results)
  }

  /**
   * Processes a single iteration of the conversational loop.
   *
   * @param iteration     Current iteration number.
   * @param request       LLM request object for the iteration.
   * @param llamaAPI      OllamaAPI client instance.
   * @param llamaModel    Model identifier for the Ollama API.
   * @param results       Mutable YAML results object.
   * @return              Optionally, the next request for subsequent iterations.
   */
  private def processIteration(
                                iteration: Int,
                                request: LlmQueryRequest,
                                llamaAPI: OllamaAPI,
                                llamaModel: String,
                                results: ListBuffer[IterationResult]
                              ): Option[LlmQueryRequest] = {
    logger.info(s"Processing iteration $iteration...")

    // Get LLM response synchronously
    val grpcResponse = Await.result(GrpcApiInvoker.get(request), 10.seconds)
    val input = request.input + " "
    val output = grpcResponse.output

    // Generate response using OllamaAPI
    val llamaResponse = llamaAPI
      .generate(
        llamaModel,
        LLAMA_PREFIX + input + output,
        false,
        new Options(Map.empty[String, Object].asJava)
      )
      .getResponse

    // Log and store results
    logger.info(s"Generated response: $llamaResponse")
    YAML_Helper.appendResult(results, iteration, input, output, llamaResponse)

    // Prepare the next request based on the generated response
    if (llamaResponse.nonEmpty) {
      Some(new LlmQueryRequest(LLAMA_TO_LAMBDA_PREFIX + llamaResponse, 100))
    } else {
      None
    }
  }

  /**
   * Initializes the OllamaAPI client with configuration settings.
   *
   * @return Configured OllamaAPI instance.
   */
  private def initializeLlamaAPI(): OllamaAPI = {
    val host = ConfigLoader.getConfig(OLLAMA_HOST)
    val timeout = ConfigLoader.getConfig(OLLAMA_REQUEST_TIMEOUT).toLong
    val llamaAPI = new OllamaAPI(host)
    llamaAPI.setRequestTimeoutSeconds(timeout)
    logger.info(s"OllamaAPI initialized with host: $host and timeout: $timeout seconds")
    llamaAPI
  }
}
