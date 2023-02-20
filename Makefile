# Written for BASH
SHELL := bash

# Set default shell flags
.SHELLFLAGS := -eu -o pipefail -c

# Run one target in one shell session for GNU make
# .ONESHELL

# Delete target file if make rule fails for GNU make
# .DELETE_ON_ERROR

# Use > instead of tab for block char for GNU make
# .RECIPEPREFIX = >

# Warm me about undefined vars early
MAKEFLAGS += --warn-undefined-variables

# Turn off built-in implicit rules (I like them to be explicit)
MAKEFLAGS += --no-builtin-rules

# Running `make` will trigger `make help`
.DEFAULT_GOAL := help

OS=`[ -f /etc/os-release ] && grep ^ID= /etc/os-release | cut -d = -f 2 || uname`
# printf formatting
BOLD="\033[1m"
NORMAL="\033[0m"

help: ## Prints <target>: <dependencies> and what it does
	@grep -E '^[a-zA-Z_-]+.*## .*$$' $(MAKEFILE_LIST) | awk 'BEGIN {FS = "## "}; {printf "\033[1m%-32s\033[0m%s\n", $$1, $$2}'

deps: ## Check deps
	@mkdir -p /tmp
	@which clojure || echo clojure , > /tmp/deps-missing
	@which geckodriver || echo geckodriver , >> /tmp/deps-missing
	@echo '*********************************************'
	@if [[ -f /tmp/deps-missing ]]; then \
          echo Please install `cat /tmp/deps-missing`; \
          rm /tmp/deps-missing; \
          exit 1; \
        else \
	 echo You have all required deps installed.; \
        fi
	@echo '*********************************************'

gcount: deps ## Visualizes google search results of "Clojure Programming" "Elixir Programming" "Elm Programming"
	clj -X:gcount
