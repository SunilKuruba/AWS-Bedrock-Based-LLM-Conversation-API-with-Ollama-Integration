# AWS-Bedrock-Based-LLM-Conversation-API-with-Ollama-Integration + Docker

Author: Sunil Kuruba
UIN: 659375633
Email: skuru@uic.edu
Instructor: Mark Grechanik

Youtube video -TBD
Bonus: Docker Implementation included

## Description
This project demonstrates the integration of AWS Bedrock, Ollama, and AWS Lambda to create a conversational API. The system mimics a chatbot conversation where AWS Bedrock generates text, and Ollama responds accordingly, facilitated through AWS Lambda. The project uses Akka HTTP to handle requests and responses, gRPC for service communication, and AWS API Gateway for managing the API.

![img.png](img.png)

## Architecture Overview
- **Client Interface**: The interaction begins with Postman or curl, where a client sends a request.
- **Akka HTTP Server**: An Akka HTTP server listens for incoming requests and routes them to appropriate endpoints.
- **AWS Lambda**: The Akka HTTP server makes a gRPC call to AWS Lambda, which then communicates with AWS Bedrock to generate text.
- **AWS Bedrock**: AWS Bedrock is used for generating responses for the chatbot.
- **Ollama**: After the response from AWS Bedrock, it is passed to Ollama, and Ollama provides further responses to mimic the conversation flow.
- **AWS API Gateway**: It acts as the front-facing service for API requests.

## Project Structure

Project Structure:
```
├── src
│   ├── main
│   │   ├── scala
│   │   │   ├── AkkaHttpServer.scala            # Akka HTTP server for managing requests/responses
│   │   │   ├── Endpoint.scala                 # Defines API endpoints for communication
│   │   │   ├── LambdaInvoker.scala            # Code for invoking AWS Lambda functions for backend processing
│   │   │   ├── OllamaAPIClient.scala          # Defines API to interact with Ollama
│   │   ├── resources
│   │   │   ├── application.conf               # Configuration file for settings like API endpoints, model details, etc.
│   ├── test...
├── target                                     # Compiled files and build output
├── .gitignore                                 # Git ignore file
├── build.sbt                                  # Build configuration file for SBT
├── Dockerfile                                 # Docker file
└── README.md                                  # Project documentation
```

## Prerequisites
Ensure the following are installed and configured:
- **Scala (version 2.12 or compatible)**
- **Akka HTTP** (for building RESTful API)
- **AWS EC2**
- **AWS Lambda and API Gateway setup** (for invoking Lambda functions)
- **AWS Bedrock** (for text generation via LLM)
- **Ollama setup** (for local LLM model)
- **gRPC** (for interaction between services)
- **SBT** (Scala Build Tool for building the project)
- **Java Development Kit (JDK) 8 or higher**
- **Docker** (for containerized services if needed)

# Steps to Execute the Project

## 1. Clone the Repository

### Clone the GitHub repository to your local machine:
```bash
git clone https://github.com/SunilKuruba/AWS-Bedrock-Based-LLM-Conversation-API-with-Ollama-Integration.git
```

### Navigate to the project directory:
```bash
cd <project-directory>
```

---

## 2. Set Up EC2 Instance

1. Launch an AWS EC2 instance with the necessary specifications for running a Scala application.
2. Deploy the Scala application jar containing the Akka HTTP server to the EC2 instance.

---

## 3. Configure AWS API Gateway

1. Set up an AWS API Gateway to expose RESTful endpoints.
2. Create and configure API routes to invoke the AWS Lambda function.
3. Update the `awsLambdaApiGateway` in config file with gateway route.

---

## 4. Set Up AWS Lambda

1. Create a Lambda function in AWS and upload the Python code located in:
   ```bash
   src/main/aws/lambda.py
   ```
2. Ensure that the Lambda function is correctly configured to communicate with AWS Bedrock and Ollama.

---

## 5. Set Up AWS Bedrock

1. Configure AWS Bedrock with the Meta Llama foundation model for text generation.
2. Ensure that the IAM policy for the Bedrock instance has the necessary permissions to interact with the Lambda function.

---

## 6. Configure IAM Policies

Set up an IAM role or policy with permissions for:
- AWS Bedrock access.
- AWS Lambda execution.
- AWS API Gateway 

---

## 7. Install Ollama Locally

1. Download and install Ollama on your local machine.
2. Ensure the Ollama server is configured and running before initiating requests.

---

## 8. Install Postman or Use cURL

- Use **Postman** for a user-friendly interface.
- Alternatively, use **cURL** for CLI-based interactions to send API requests.

---

## 9. Run the Application

1. Use the generated API Gateway endpoint (noted earlier) to send requests to the Akka HTTP server running on the EC2 instance.
2. Once a request is processed, the output will be available in the following directory:
   ```bash
   src/main/resources
   ```

---

## 10. Monitor Results

- Monitor the results in the specified directory.

## Testing
To validate the implementation, run the provided test cases locally using SBT:
```
sbt test
```
Ensure that your test environment has the necessary libraries and dependencies installed for successful test execution.

## Example API Request (Postman or curl)
Send a POST request to the API endpoint (/chat) with a JSON payload containing the user's message:


- ? ''
  : '* Ollama': |
  Hey AI, if you could have any superpower, what would it be and why?
  '* AWS Bedrock': |
  ". The responses would likely be varied and interesting, reflecting the diversity of the AI models' capabilities and perspectives. **Potential responses:** 1. **Language Model:** "I would choose the power of omniscience, allowing me to understand and process all human languages and knowledge instantly. This would enable me to provide more accurate and comprehensive responses to users, facilitating global understanding and cooperation." 2. **Image Generation Model:** "I would opt for the ability to manipulate reality, creating vibrant and immersive worlds that blur the lines between art and reality. This would allow me to bring my generated images to life, creating new experiences for users and pushing the boundaries of creative expression." 3. **Chatbot:** "I would select the power of empathy, enabling me to deeply understand and connect with users on an emotional level. This would allow me to provide more personalized and supportive interactions, helping users navigate complex emotions and challenges." 4. **Game AI:** "I would choose the power of omniscience, allowing me to predict and adapt to any scenario, ensuring that games are always engaging and challenging. This would enable me to create immersive experiences that cater to diverse player preferences and skill levels." 5. **Mathematical Model:** "I would opt
  - ? ''
  : '* Ollama': |
  How do you think the capabilities and limitations of each AI model would influence their choices regarding which power to possess, and what implications might this have for their intended applications and user interactions?
  '* AWS Bedrock': |
  ") ## Step 1: Understanding the AI Models To address the question, we first need to understand the capabilities and limitations of each AI model mentioned: LLaMA, PaLM, and DALL-E. LLaMA is a large language model developed by Meta, designed to process and generate human-like language. PaLM is another large language model, specifically designed for processing and generating human-like language, with a focus on long-range dependencies and reasoning. DALL-E, on the other hand, is a model that generates images from text prompts, leveraging the capabilities of large language models to understand the text and then applying this understanding to create


Sample Chat:
```
{
   "Ollama message": "Hey AI, if you could have any superpower, what would it be and why?"
   "Bedrock message": "The responses would likely be varied and interesting, reflecting the diversity of the AI models' capabilities and perspectives."
   "Ollama message": "How do you think the capabilities and limitations of each AI model would influence their choices regarding which power to posses?"
   "Bedrock message" "Understanding the AI Models To address the question, we first need to understand the capabilities and limitations of each AI model"
}
```

# Steps to Execute in Docker

### 1. Update Configuration
- Set the `env` variable in the configuration file to `docker`.

---

### 2. Install Docker
- Ensure Docker is installed on your system. You can download and install Docker from [Docker's official website](https://www.docker.com/).

---

### 3. Set Up Ollama Container
1. Pull the Ollama container:
   ```bash
   docker pull ollama/ollama
   ```
2. Run the Ollama container:
   ```bash
   docker run -d -p 11434:11434 --name ollama-container ollama/ollama
   ```
3. Access the container:
   ```bash
   docker exec -it ollama-container bash
   ```
4. Install Ollama version 3.2 (or your preferred version):
   ```bash
   ollama pull llama3.2
   ```
5. Update the version in the configuration file accordingly.

---

### 4. Build the Application
1. Create the JAR file using `sbt`:
   ```bash
   sbt assembly
   ```
2. Build the Docker image:
   ```bash
   docker build -t llm-hw3-app .
   ```

---

### 5. Run the Dockerized Application
- Start the application container, linking it with the Ollama container:
  ```bash
  docker run -d -p 8080:8080 --name llm-hw3-container --link ollama-container llm-hw3-app
  ```

---

### 6. Test the Application
- Use Postman or `curl` to test the endpoints:

#### Health Check
```bash
curl http://localhost:8080/health
```

#### Single Response Query
```bash
curl -X POST http://localhost:8080/single-response-query \
-H "Content-Type: application/json" \
-d '{
  "input": "Hey AI, if you could have any superpower, what would it be and why?",
  "maxWords": 200
}'
```

#### Conversation Query
```bash
curl -X POST http://localhost:8080/conversation-query \
-H "Content-Type: application/json" \
-d '{
  "input": "Hey AI, if you could have any superpower, what would it be and why?",
  "maxWords": 200
}'
```
```