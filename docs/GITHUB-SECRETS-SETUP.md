# GitHub Secrets Setup Guide

## 🔐 Required GitHub Repository Secrets

To complete the DevOps pipeline setup, you need to add the following secrets to your GitHub repository.

### How to Add Secrets
1. Go to your GitHub repository
2. Navigate to `Settings` → `Secrets and variables` → `Actions`
3. Click `New repository secret`
4. Add each secret below

---

## 📊 SonarQube Configuration

### SONAR_TOKEN
```
squ_ab08334243b8ac91442f39c7f7a2fa1cde336f8b
```
**Description**: Authentication token for SonarQube API access

### SONAR_HOST_URL
```
http://sonar.cvetochey.ru
```
**Description**: SonarQube server URL (update this based on your ingress configuration)

**Alternative for testing** (if ingress not configured):
```
http://89.169.138.79:9000
```

---

## ☁️ Yandex Cloud Configuration

### YC_SERVICE_ACCOUNT_KEY
**Description**: Base64-encoded service account key for Yandex Cloud access
**Format**: Base64 encoded JSON key

**To get this value:**
```bash
# If you have the key file
cat path/to/your/service-account-key.json | base64

# Or create a new service account key
yc iam service-account create --name github-actions-sa
yc resource-manager folder add-access-binding <folder-id> \
  --role editor \
  --subject serviceAccount:<service-account-id>
yc iam key create --service-account-name github-actions-sa \
  --output key.json
cat key.json | base64
```

### YC_CLOUD_ID
**Description**: Your Yandex Cloud ID
```bash
# Get your cloud ID
yc config list
```

### YC_FOLDER_ID
**Description**: Your Yandex Cloud folder ID
```bash
# Get your folder ID
yc config list
```

---

## 📱 Telegram Bot Configuration

### TELEGRAM_BOT_TOKEN
**Description**: Bot token from @BotFather
**Format**: `1234567890:ABCdefGHIjklMNOpqrsTUVwxyz`

**To get this value:**
1. Message @BotFather on Telegram
2. Send `/newbot`
3. Follow instructions to create your bot
4. Copy the token provided

### TELEGRAM_CHAT_ID
**Description**: Chat ID where notifications will be sent
**Format**: `-1001234567890` (for groups) or `123456789` (for private chats)

**To get this value:**
1. Add your bot to the target group/channel
2. Send a test message to the bot
3. Visit: `https://api.telegram.org/bot<BOT_TOKEN>/getUpdates`
4. Find the `chat.id` in the response

---

## 📈 Optional: Code Coverage Integration

### CODECOV_TOKEN
**Description**: Token for Codecov integration (optional)
**How to get**: Sign up at codecov.io and get your repository token

---

## ✅ Complete Secrets List

Here's the complete list of secrets to add:

| Secret Name | Required | Description |
|-------------|----------|-------------|
| `SONAR_TOKEN` | ✅ Yes | SonarQube authentication |
| `SONAR_HOST_URL` | ✅ Yes | SonarQube server URL |
| `TELEGRAM_BOT_TOKEN` | ✅ Yes | Telegram bot token |
| `TELEGRAM_CHAT_ID` | ✅ Yes | Telegram chat ID |
| `YC_SERVICE_ACCOUNT_KEY` | ✅ Yes | Yandex Cloud access |
| `YC_CLOUD_ID` | ✅ Yes | Yandex Cloud ID |
| `YC_FOLDER_ID` | ✅ Yes | Yandex folder ID |
| `CODECOV_TOKEN` | ❌ Optional | Code coverage reporting |

---

## 🧪 Testing Secrets

After adding all secrets, you can test them by:

1. **Push to dev branch** - triggers CI pipeline with SonarQube analysis
2. **Create a pull request** - triggers notifications
3. **Check workflow logs** - verify secrets are working

---

## 🔧 Troubleshooting

### Common Issues

**SonarQube Connection Failed**
- Check `SONAR_HOST_URL` is accessible
- Verify `SONAR_TOKEN` is correct
- Ensure SonarQube is running

**Yandex Cloud Authentication Failed**
- Verify service account has proper permissions
- Check `YC_SERVICE_ACCOUNT_KEY` is base64 encoded
- Ensure cloud and folder IDs are correct

**Telegram Notifications Not Working**
- Verify bot token is correct
- Check chat ID format (include `-` for groups)
- Ensure bot is added to the target chat

---

## 📋 Next Steps

After adding all secrets:
1. ✅ Push a commit to `dev` branch
2. ✅ Check GitHub Actions workflow
3. ✅ Verify SonarQube analysis
4. ✅ Test Telegram notifications
5. ✅ Validate deployment pipeline

---

**Status**: Ready for secrets configuration!
