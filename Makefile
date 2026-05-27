SHELL := /bin/bash
API := apps/api
WEB := apps/web

.PHONY: help build test test-web typecheck-web lint-web lint-java fmt up down e2e bench bench-regress images

help:
	@grep -E '^[a-zA-Z_-]+:.*?## .*$$' $(MAKEFILE_LIST) | awk 'BEGIN {FS = ":.*?## "}; {printf "%-16s %s\n", $$1, $$2}'

build: ## Compile the backend
	cd $(API) && ./mvnw -q -B package -DskipTests

test: ## Backend unit + integration tests
	cd $(API) && ./mvnw -B test

lint-java: ## Spotless format check
	cd $(API) && ./mvnw -B spotless:check

fmt: ## Apply spotless formatting
	cd $(API) && ./mvnw -B spotless:apply

lint-web: ## Frontend lint
	cd $(WEB) && npm run lint

typecheck-web: ## Frontend type check
	cd $(WEB) && npm run typecheck

test-web: ## Frontend unit tests
	cd $(WEB) && npm run test

e2e: ## Playwright end-to-end against docker-compose
	cd $(WEB) && npm run e2e

bench: ## Run the full-scale answer-throughput benchmark (1M submissions)
	cd $(API) && ./mvnw -B -Pbench test -Dtest=AnswerThroughputBench

bench-regress: ## Fail if throughput regresses beyond 30% of baseline
	cd $(API) && ./mvnw -B -Pbench test -Dtest=BenchRegressionTest

up: ## Start postgres + api + web
	docker compose up --build -d

down: ## Stop the stack
	docker compose down -v

images: ## Build api + web images
	docker compose build
