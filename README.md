# QGPT - Natural Language to SQL Query Generator

[![License: MIT](https://img.shields.io/badge/License-MIT-yellow.svg)](https://opensource.org/licenses/MIT)

QGPT is a powerful tool that converts natural language questions into executable SQL queries by leveraging large language models (LLMs). This application automatically understands your database schema and generates accurate SQL based on simple English questions.

## Features

- **Natural Language Understanding**: Transform plain English questions into SQL queries
- **Automatic Schema Detection**: Analyzes and understands your database structure
- **Multiple Database Support**: Works with MySQL, PostgreSQL, SQL Server, and more
- **LLM Integration**: Uses powerful language models to generate accurate queries
- **RESTful API**: Simple HTTP endpoints for integration with any application
- **Security**: Built-in validation to prevent SQL injection

## Quick Start

### Prerequisites

- Java 17 or higher
- Gradle
- A SQL database (MySQL, PostgreSQL, SQL Server, etc.)
- API key for your preferred LLM service (OpenAI, Mistral, etc.)

### Installation

1. Clone the repository:
   ```bash
   git clone https://github.com/kunalmittal51/qgpt.git
   cd qgpt
   ```

2. Configure your database and LLM API keys in `src/main/resources/application.properties`:
   ```properties
   spring.datasource.url=jdbc:postgresql://localhost:5432/your_database
   spring.datasource.username=your_username
   spring.datasource.password=your_password
   
   llm.api.key=your_api_key
   llm.api.url=https://api.example.com/v1/chat/completions
   ```

3. Build and run the application:
   ```bash
   ./gradlew bootRun
   ```

## Usage

### REST API

#### Generate SQL Query

```
POST /api/query
```

**Request Body:**
```json
{
  "naturalLanguageQuery": "Show me all customers from New York who have placed orders in the last month"
}
```

**Response:**
```json
{
  "generatedSql": "SELECT c.* FROM customers c JOIN orders o ON c.id = o.customer_id WHERE c.state = 'New York' AND o.order_date >= DATE_SUB(CURDATE(), INTERVAL 1 MONTH)",
  "results": [
    {
      "id": 42,
      "name": "John Doe",
      "email": "john@example.com",
      "state": "New York"
    },
    ...
  ]
}
```

## Architecture

QGPT is built on a clean, modular architecture:

1. **SchemaService**: Connects to your database, analyzes schema, and provides context to the LLM
2. **LLMService**: Handles communication with the language model API
3. **QueryExecutionService**: Safely validates and executes generated SQL
4. **QueryController**: Exposes RESTful endpoints for client applications

