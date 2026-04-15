# **College Help Desk - RAG Based AI Query System**

[![Java](https://img.shields.io/badge/Java-21-blue.svg)](https://www.java.com)
[![Spring Boot](https://img.shields.io/badge/Spring%20Boot-3.x-brightgreen.svg)](https://spring.io/projects/spring-boot)
[![Docker](https://img.shields.io/badge/Docker-Ready-blue.svg)](https://www.docker.com/)

College Help Desk is a Retrieval-Augmented Generation (RAG) based AI application designed to provide accurate and context-aware answers to queries related to IIIT Bhopal. It enables users to ask questions about notices, faculty details, and other institutional information, leveraging real-time data ingestion and intelligent retrieval mechanisms.

## **Architecture Overview**

The system is built as a **monolithic backend application** while internally leveraging modern scalable components like vector search, caching, and concurrent processing.

### **Core Components**

* **RAG Pipeline:** Retrieves relevant context from vector storage and generates accurate responses using an LLM.
* **ETL Pipeline:** Continuously ingests and processes college data (notices and faculty information).
* **Vector Search (Elasticsearch):** Stores embeddings for semantic retrieval.
* **Caching Layer (Redis + Vector Search):** Optimizes repeated queries using similarity-based cache retrieval.
* **Concurrency Engine (Virtual Threads):** Executes multiple stages of processing in parallel for high performance.

## **Key Highlights**

* **RAG-Based Intelligent Query System:** Combines retrieval and generation to provide highly accurate, context-aware answers.
* **Automated ETL Pipeline:** Periodically ingests notices and supports manual ingestion for faculty data.
* **Hybrid Vector Strategy:** Uses Elasticsearch for primary knowledge retrieval and Redis Vector for query-level caching.
* **Similarity-Based Caching:** Incoming queries are matched against cached embeddings using a threshold to return instant responses.
* **Concurrent Processing with Virtual Threads:** Cache lookup and full pipeline execution run in parallel using dedicated executors.
* **Early Response Optimization:** Returns cached answers immediately if similarity threshold is satisfied.
* **Conversation-Based Query Handling:** Supports chat history using `conversationId`.

## **Features**
* Currently, can answer queries related to:
  - IIIT Bhopal notices
  - Faculty information
* Chat-based interaction with conversation tracking
* Periodic ingestion of latest notices (every 6 hours)
* Manual ingestion of faculty portfolio data
* Semantic cahce search using embeddings
* High-performance concurrent execution using virtual threads

## **Data Pipeline (RAG + ETL)**

### **Data Sources**

* IIIT Bhopal Official Website

### **ETL Flow**

1. Extract data from website (API / HTML pages)
2. Convert content into structured text
3. Split text into overlapping chunks (text splitter)
4. Generate embeddings
5. Store processed data in vector database

### **Ingestion Types**

* **Notices:** Automatically fetched every 6 hours, filter for only those notices which were not processed according to last_processed_date variable
* **Faculty:** Manually ingested via API

## **Storage Strategy**

* **Elasticsearch (Vector Store):**

  * Stores processed institutional data
  * Used for primary semantic retrieval

* **Redis Vector Store:**

  * Stores:

    * Query
    * Query embedding
    * Final generated answer
  * Used for similarity-based caching

## **Query Processing Flow**

1. User sends query
2. Query embedding is generated
3. Parallel execution starts:

   * **Cache Search (Redis Vector)**
   * **Full RAG Pipeline**

     * Query classification
     * Context retrieval (Elasticsearch)
     * Prompt generation
     * LLM response
4. If cache similarity threshold is met → return cached response
5. Otherwise → return newly generated response and cache it

## **API Endpoints**

### **Query API**

```http
POST /ask/{conversationId}/{modelName}
```

* Accepts user query
* Returns AI-generated response
* Maintains chat history

### **Notice Ingestion API**

```http
POST /admin/ingest/notices?url=<notice_url>
```

* Triggers ingestion of notices manually

### **Scheduled Ingestion**

* Automatically runs every 6 hours:

```cron
0 0 */6 * * *
```

## **Tech Stack**

* **Backend:** Java 21, Spring Boot
* **AI Layer:** Spring AI, RAG
* **Search & Vector DB:** Elasticsearch
* **Caching:** Redis (Normal + Vector)
* **Concurrency:** Virtual Threads
* **Containerization:** Docker, Docker Compose

## **Prerequisites**

Before running the project, ensure you have:

* Docker & Docker Compose installed
* Git installed
* A valid Google API Key (for LLM access)

## **Quick Start**

### **1. Clone the Repository**

```bash
git clone <your-repo-url>
cd <your-project-folder>
```

### **2. Create `.env` File**

Create a `.env` file in the root directory and add:

```ini
DOCKER_USERNAME=sanskarpatidar
ELASTICSEARCH_HOST=elasticsearch
REDIS_HOST=redis-stack
GOOGLE_API_KEY=your-own-api-key
```

### **3. Run the Application**

```bash
docker-compose -f docker-compose.yaml up -d
```

### **4. Verify Services**

```bash
docker-compose ps
```

All services should be running.

## **Supported Query Domains**

Currently, the system supports:

* **Notices**
* **Faculty Information**

(Future scope: Expand to academics, hostel, placements, etc.)

## **Future Improvements**

* Add more data domains (academics, syllabus, placements)
* Improve ranking and retrieval accuracy
* UI-based chatbot interface
* Multi-model support
* Advanced query classification

## **Stopping the Application**

```bash
docker-compose down -v
```


