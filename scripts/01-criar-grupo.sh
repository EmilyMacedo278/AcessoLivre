#!/usr/bin/env bash
set -e

az account set --subscription "ProfAntonioFigueiredo2Semestre2026"

az group create \
  --name rg-acessolivre-rm554882 \
  --location southafricanorth \
  --output table
