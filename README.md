# Tier calculator

This is an application that received the reports (webhook) about the customers' orders and uses that
data to calculate their loyalty tier.

## Main concepts

- The backend is exposing a REST API
- The monetary values from the backend are reported in cents.
- The tier is represented as an integer value - it's up to the frontend to display it as for example
  a 🥇

## Documentation

Specific documentation is in the [Backend](/backend) and [Frontend](/frontend) directories. Backend
includes an [OpenAPIv3 specification](/backend/openapi.yml).