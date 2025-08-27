# Expense Tracker Makefile

.PHONY: help build test run clean up down logs backend-test backend-build

# Default target
help: ## Show this help message
	@echo "Available targets:"
	@awk 'BEGIN {FS = ":.*?## "} /^[a-zA-Z_-]+:.*?## / {printf "  %-15s %s\n", $$1, $$2}' $(MAKEFILE_LIST)

# Docker Compose Operations
up: ## Start all services with docker-compose
	docker-compose up -d

down: ## Stop all services
	docker-compose down

logs: ## Show logs from all services
	docker-compose logs -f

# Backend Operations
backend-build: ## Build the backend JAR
	cd backend && mvn clean package -DskipTests

backend-test: ## Run backend tests
	cd backend && mvn test

backend-run: ## Run backend locally (requires MongoDB and Redis)
	cd backend && mvn spring-boot:run

# Build Operations
build: backend-build ## Build all components
	docker-compose build

# Test Operations
test: backend-test ## Run all tests

# Development Operations
run: up ## Alias for 'up' - start all services
	@echo "Services are starting. Backend will be available at http://localhost:8080"
	@echo "Swagger UI will be available at http://localhost:8080/swagger-ui.html"
	@echo "MongoDB: localhost:27017"
	@echo "Redis: localhost:6379"
	@echo "MinIO: localhost:9000 (console: localhost:9001)"

clean: ## Clean build artifacts and Docker resources
	cd backend && mvn clean
	docker-compose down -v
	docker system prune -f

# Setup Operations
setup: ## Initial setup - build and start services
	make backend-build
	make build
	make up
	@echo "Setup complete! Services are running."

# Smoke Tests
smoke-test: ## Run smoke tests against running services
	@echo "Running smoke tests..."
	@if ! command -v jq &> /dev/null; then \
		echo "jq is required for smoke tests. Please install jq."; \
		exit 1; \
	fi
	./scripts/smoke-test.sh

# Quick development cycle
dev: ## Quick development cycle - build and restart
	make down
	make backend-build
	make up

# Logs for specific services
backend-logs: ## Show backend logs
	docker-compose logs -f backend

mongo-logs: ## Show MongoDB logs
	docker-compose logs -f mongodb

redis-logs: ## Show Redis logs
	docker-compose logs -f redis