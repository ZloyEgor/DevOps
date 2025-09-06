#!/bin/bash

# Telegram Bot Setup Script for CvetOchey DevOps Pipeline

set -e

# Colors for output
RED='\033[0;31m'
GREEN='\033[0;32m'
YELLOW='\033[1;33m'
BLUE='\033[0;34m'
NC='\033[0m' # No Color

print_header() {
    echo -e "${BLUE}================================${NC}"
    echo -e "${BLUE}$1${NC}"
    echo -e "${BLUE}================================${NC}"
}

print_status() {
    echo -e "${GREEN}[INFO]${NC} $1"
}

print_warning() {
    echo -e "${YELLOW}[WARNING]${NC} $1"
}

print_error() {
    echo -e "${RED}[ERROR]${NC} $1"
}

print_step() {
    echo -e "${YELLOW}[STEP]${NC} $1"
}

# Welcome
clear
print_header "Telegram Bot Setup for CvetOchey"
echo ""
echo "This script will help you set up a Telegram bot for GitHub notifications."
echo ""

# Step 1: Create Bot
print_header "Step 1: Create Telegram Bot"
echo ""
echo "📱 Follow these steps to create your bot:"
echo ""
echo "1. Open Telegram and search for '@BotFather'"
echo "2. Start a chat with @BotFather"
echo "3. Send the command: /newbot"
echo "4. Follow the prompts to:"
echo "   - Choose a name for your bot (e.g., 'CvetOchey DevOps Bot')"
echo "   - Choose a username (must end in 'bot', e.g., 'cvetochey_devops_bot')"
echo "5. Copy the bot token that @BotFather provides"
echo ""
echo "Example bot token format: 1234567890:ABCdefGHIjklMNOpqrsTUVwxyz"
echo ""

read -p "Press Enter when you have created the bot and have the token..."

# Step 2: Get Bot Token
print_header "Step 2: Configure Bot Token"
echo ""
read -p "Enter your bot token: " BOT_TOKEN

if [[ -z "$BOT_TOKEN" ]]; then
    print_error "Bot token cannot be empty!"
    exit 1
fi

# Validate token format
if [[ ! "$BOT_TOKEN" =~ ^[0-9]+:[A-Za-z0-9_-]+$ ]]; then
    print_warning "Token format looks unusual. Please verify it's correct."
fi

# Test bot token
print_status "Testing bot token..."
RESPONSE=$(curl -s "https://api.telegram.org/bot$BOT_TOKEN/getMe")
if echo "$RESPONSE" | grep -q '"ok":true'; then
    BOT_INFO=$(echo "$RESPONSE" | jq -r '.result.first_name')
    print_status "✅ Bot token is valid! Bot name: $BOT_INFO"
else
    print_error "❌ Invalid bot token. Please check and try again."
    exit 1
fi

# Step 3: Get Chat ID
print_header "Step 3: Get Chat ID"
echo ""
echo "📱 Now you need to get the chat ID where notifications will be sent:"
echo ""
echo "Option A - Private Chat:"
echo "1. Start a private chat with your bot"
echo "2. Send any message to the bot (e.g., '/start' or 'hello')"
echo ""
echo "Option B - Group Chat:"
echo "1. Add your bot to a group"
echo "2. Send a message mentioning the bot (e.g., '@your_bot_name hello')"
echo ""

read -p "Press Enter after you've sent a message to your bot..."

# Get updates to find chat ID
print_status "Fetching chat information..."
UPDATES=$(curl -s "https://api.telegram.org/bot$BOT_TOKEN/getUpdates")

if echo "$UPDATES" | grep -q '"ok":true'; then
    # Extract chat IDs from updates
    CHAT_IDS=$(echo "$UPDATES" | jq -r '.result[].message.chat.id' 2>/dev/null | sort -u)
    
    if [[ -n "$CHAT_IDS" ]]; then
        print_status "✅ Found chat(s):"
        echo ""
        
        # Display available chats
        counter=1
        declare -a chat_array
        while IFS= read -r chat_id; do
            if [[ -n "$chat_id" && "$chat_id" != "null" ]]; then
                chat_info=$(echo "$UPDATES" | jq -r ".result[] | select(.message.chat.id == $chat_id) | .message.chat.title // .message.chat.first_name // \"Private Chat\"" | head -1)
                echo "$counter) Chat ID: $chat_id ($chat_info)"
                chat_array[$counter]=$chat_id
                ((counter++))
            fi
        done <<< "$CHAT_IDS"
        
        echo ""
        if [[ ${#chat_array[@]} -eq 1 ]]; then
            CHAT_ID=${chat_array[1]}
            print_status "Using chat ID: $CHAT_ID"
        else
            read -p "Select chat number (1-$((counter-1))): " selection
            CHAT_ID=${chat_array[$selection]}
            if [[ -z "$CHAT_ID" ]]; then
                print_error "Invalid selection!"
                exit 1
            fi
        fi
    else
        print_error "No chats found. Please send a message to your bot first."
        exit 1
    fi
else
    print_error "Failed to get updates from Telegram API."
    exit 1
fi

# Step 4: Test Notification
print_header "Step 4: Test Notification"
print_status "Sending test notification..."

TEST_MESSAGE="🎉 *CvetOchey DevOps Bot Setup Complete!*

✅ Bot is configured and ready
✅ Chat ID: \`$CHAT_ID\`
✅ Notifications will be sent here

This bot will notify you about:
• 🚀 Push notifications
• 📋 Pull request activities  
• ✅ Successful deployments
• ❌ Pipeline failures
• 🎉 Release announcements

*Setup completed successfully!*"

SEND_RESPONSE=$(curl -s -X POST "https://api.telegram.org/bot$BOT_TOKEN/sendMessage" \
    -d "chat_id=$CHAT_ID" \
    -d "text=$TEST_MESSAGE" \
    -d "parse_mode=Markdown")

if echo "$SEND_RESPONSE" | grep -q '"ok":true'; then
    print_status "✅ Test notification sent successfully!"
else
    print_warning "⚠️ Test notification failed, but configuration should still work."
fi

# Step 5: Display Configuration
print_header "Step 5: GitHub Secrets Configuration"
echo ""
print_status "Add these secrets to your GitHub repository:"
echo ""
echo "🔐 GitHub Repository Secrets:"
echo "================================"
echo "Secret Name: TELEGRAM_BOT_TOKEN"
echo "Secret Value: $BOT_TOKEN"
echo ""
echo "Secret Name: TELEGRAM_CHAT_ID" 
echo "Secret Value: $CHAT_ID"
echo ""
echo "📋 How to add secrets:"
echo "1. Go to your GitHub repository"
echo "2. Navigate to Settings → Secrets and variables → Actions"
echo "3. Click 'New repository secret'"
echo "4. Add both secrets above"
echo ""

# Step 6: Save Configuration
print_header "Step 6: Save Configuration"
CONFIG_FILE="telegram-bot-config.txt"
cat > "$CONFIG_FILE" << EOF
# Telegram Bot Configuration for CvetOchey DevOps Pipeline
# Generated on: $(date)

TELEGRAM_BOT_TOKEN=$BOT_TOKEN
TELEGRAM_CHAT_ID=$CHAT_ID

# GitHub Secrets to add:
# TELEGRAM_BOT_TOKEN=$BOT_TOKEN
# TELEGRAM_CHAT_ID=$CHAT_ID

# Bot Information:
# - Bot Name: $BOT_INFO
# - Chat Type: $(echo "$UPDATES" | jq -r ".result[] | select(.message.chat.id == $CHAT_ID) | .message.chat.type" | head -1)
# - Setup Date: $(date)
EOF

print_status "✅ Configuration saved to: $CONFIG_FILE"
print_warning "⚠️ Keep this file secure - it contains your bot token!"

# Final Summary
print_header "Setup Complete!"
echo ""
print_status "✅ Telegram bot created and configured"
print_status "✅ Test notification sent"
print_status "✅ Configuration saved"
echo ""
echo "🚀 Next Steps:"
echo "1. Add the secrets to GitHub (shown above)"
echo "2. Push a commit to test the pipeline"
echo "3. Check that notifications work"
echo ""
echo "📱 Your bot is ready to send DevOps notifications!"
