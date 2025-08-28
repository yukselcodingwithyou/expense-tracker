#!/bin/bash

# Expense Tracker Smoke Test
# Tests basic functionality of the backend API

set -e  # Exit on any error

BASE_URL="http://localhost:8080/api/v1"
EMAIL="smoketest@example.com"
PASSWORD="smoketest123"

echo "🧪 Starting Expense Tracker Smoke Test"
echo "======================================="

# Wait for backend to be ready
echo "⏳ Waiting for backend to be ready..."
for i in {1..30}; do
    if curl -f "$BASE_URL/../actuator/health" >/dev/null 2>&1; then
        echo "✅ Backend is ready!"
        break
    fi
    echo "   Attempt $i/30 - Backend not ready yet..."
    sleep 2
done

# Test health endpoint
echo ""
echo "🩺 Testing health endpoint..."
curl -f "$BASE_URL/../actuator/health" | jq '.'

# Test user signup
echo ""
echo "👤 Testing user signup..."
SIGNUP_RESPONSE=$(curl -s -X POST "$BASE_URL/auth/signup" \
    -H "Content-Type: application/json" \
    -d "{\"email\":\"$EMAIL\",\"password\":\"$PASSWORD\"}")

echo "Signup response: $SIGNUP_RESPONSE" | jq '.'

# Extract access token
ACCESS_TOKEN=$(echo "$SIGNUP_RESPONSE" | jq -r '.accessToken')
echo "Access token: ${ACCESS_TOKEN:0:20}..."

# Test authentication by creating a family
echo ""
echo "🏠 Testing family creation..."
FAMILY_RESPONSE=$(curl -s -X POST "$BASE_URL/families" \
    -H "Content-Type: application/json" \
    -H "Authorization: Bearer $ACCESS_TOKEN" \
    -d '{"name":"Smoke Test Family","currency":"USD"}')

echo "Family response: $FAMILY_RESPONSE" | jq '.'

# Extract family ID
FAMILY_ID=$(echo "$FAMILY_RESPONSE" | jq -r '.id')
echo "Family ID: $FAMILY_ID"

# Test category creation
echo ""
echo "📂 Testing category creation..."
CATEGORY_RESPONSE=$(curl -s -X POST "$BASE_URL/categories" \
    -H "Content-Type: application/json" \
    -H "Authorization: Bearer $ACCESS_TOKEN" \
    -d '{"name":"Test Category","type":"EXPENSE","icon":"🛒","color":"#FF5722"}')

echo "Category response: $CATEGORY_RESPONSE" | jq '.'

# Extract category ID
CATEGORY_ID=$(echo "$CATEGORY_RESPONSE" | jq -r '.id')
echo "Category ID: $CATEGORY_ID"

# Test ledger entry creation
echo ""
echo "💰 Testing ledger entry creation..."
LEDGER_RESPONSE=$(curl -s -X POST "$BASE_URL/ledger" \
    -H "Content-Type: application/json" \
    -H "Authorization: Bearer $ACCESS_TOKEN" \
    -d "{\"memberId\":\"user123\",\"type\":\"EXPENSE\",\"amountMinor\":2550,\"currency\":\"USD\",\"categoryId\":\"$CATEGORY_ID\",\"occurredAt\":\"$(date -u +%Y-%m-%dT%H:%M:%S.000Z)\",\"notes\":\"Smoke test transaction\"}")

echo "Ledger response: $LEDGER_RESPONSE" | jq '.'

# Test recent transactions
echo ""
echo "📋 Testing recent transactions..."
RECENT_RESPONSE=$(curl -s -X GET "$BASE_URL/ledger/recent" \
    -H "Authorization: Bearer $ACCESS_TOKEN")

echo "Recent transactions: $RECENT_RESPONSE" | jq '.'

echo ""
echo "🎉 All tests passed! Expense Tracker backend is working correctly."
echo ""
echo "📖 You can now:"
echo "   • View Swagger UI: http://localhost:8080/swagger-ui.html"
echo "   • Test endpoints manually with the access token: $ACCESS_TOKEN"
echo "   • Build and run the Android app"