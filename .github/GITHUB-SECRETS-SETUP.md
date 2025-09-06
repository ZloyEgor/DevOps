# 🔐 GitHub Secrets Setup for Yandex Cloud CI/CD

## 📋 Required Secrets

You need to add these **3 secrets** to your GitHub repository:

### 1. `YC_CLOUD_ID`
```
b1g1hh4a73qpbdr1kbbf
```

### 2. `YC_FOLDER_ID`  
```
b1gs0cg1voiht42pp513
```

### 3. `YC_SERVICE_ACCOUNT_KEY`
```
ewogICAiaWQiOiAiYWpldTkyaTRraHFwbzE2dGdvaHQiLAogICAic2VydmljZV9hY2NvdW50X2lkIjogImFqZTFvam1sZ3I3cmVtNW9lZ2loIiwKICAgImNyZWF0ZWRfYXQiOiAiMjAyNS0wOS0wNFQyMjoxOTowNC44OTc5MjUzNDhaIiwKICAgImtleV9hbGdvcml0aG0iOiAiUlNBXzIwNDgiLAogICAicHVibGljX2tleSI6ICItLS0tLUJFR0lOIFBVQkxJQyBLRVktLS0tLVxuTUlJQklqQU5CZ2txaGtpRzl3MEJBUUVGQUFPQ0FROEFNSUlCQ2dLQ0FRRUFvVGxlK29oeVZGMHdaRnJJd2NiV1xuTEtURkZHdFJwaFRKK0M2ZVo4SmZzT0pZSEZNenNwUVdERnhpQnkyanBSaVFZcVMxZmZVcEUrWC9QYzNuWmY5blxucDR4Tjl4WFlnQW5mUE1zM3lMeGxUaE9uSUZOUzFrN3gyS1UrbkRaMUJ2QXVERzVNbkdNOVErWW1pR3dERjJSaVxucGc1bDFWZU1kWk9NbU42aWpOU3lDTGk1ZllKeTZaZ1RLWXZKRGh1UDMxSUszU21wWHpEVVRIZ09Ta1JXNTAxQVxuYVdocWE5VTR1ejduZ2JKR3NFaTh5czdKenV2QlRzQ2V3Zjk2RFlBK3JsaGRhNkMwYkx2U1R1SzZ3THhBMmR3RlxuZU1GeVZ1UURFMHJyZGgrLzdHVGNFM250NGh2aG9Ccy82V2ZoNEFndEJTMWhXMVlseENlR2xvbFdISnNrTHJaVFxuOVFJREFRQUJcbi0tLS0tRU5EIFBVQkxJQyBLRVktLS0tLVxuIiwKICAgInByaXZhdGVfa2V5IjogIlBMRUFTRSBETyBOT1QgUkVNT1ZFIFRISVMgTElORSEgWWFuZGV4LkNsb3VkIFNBIEtleSBJRCBcdTAwM2NhamV1OTJpNGtocXBvMTZ0Z29odFx1MDAzZVxuLS0tLS1CRUdJTiBQUklWQVRFIEtFWS0tLS0tXG5NSUlFdlFJQkFEQU5CZ2txaGtpRzl3MEJBUUVGQUFTQ0JLY3dnZ1NqQWdFQUFvSUJBUUNoT1Y3NmlISlVYVEJrXG5Xc2pCeHRZc3BNVVVhMUdtRk1uNExwNW53bCt3NGxnY1V6T3lsQllNWEdJSExhT2xHSkJpcExWOTlTa1Q1Zjg5XG56ZWRsLzJlbmpFMzNGZGlBQ2Q4OHl6Zkl2R1ZPRTZjZ1UxTFdUdkhZcFQ2Y05uVUc4QzRNYmt5Y1l6MUQ1aWFJXG5iQU1YWkdLbURtWFZWNHgxazR5WTNxS00xTElJdUxsOWduTHBtQk1waThrT0c0L2ZVZ3JkS2FsZk1OUk1lQTVLXG5SRmJuVFVCcGFHcHIxVGk3UHVlQnNrYXdTTHpLenNuTzY4Rk93SjdCLzNvTmdENnVXRjFyb0xSc3U5Sk80cnJBXG52RURaM0FWNHdYRlc1QU1UU3V0Mkg3L3NaTndUZWUzaUcrR2dHei9wWitIZ0NDMEZMV0ZiVmlYRUo0YVdpVlljXG5teVF1dGxQMUFnTUJBQUVDZ2dFQUhtNllYWHowNjdTOVppdjJ1aEVBcTJPY3FicUtLbW5CSjk0UTIzQUdwUHBNXG5mR2crR1dhM2RMVFpjaFpzemNTQnVJVHpseWQ0RGYvN2VseUw0YVpnbC9FMVpEN1U0M0k1bDUyYVZZTXVNaHNtXG5rRVQ2SFdiQVlzN3FMdzFlY0tWVVk5ZE1FYmJ2d1FuSDJ6ZGNMQVd3VXp6amZ1NHNIcU52eGdrOE1JNFRYdTdHXG5mN1NUZGQzNm42M0ZnTDdOK1dGeDlURzU1L292VUFLMDBKcnhxQjJBZHFwdXFpd2U0eFZESVhVaGRyNjFhMGowXG5ZaTVFSHdqNSttREJSSXcvRnc2cjZOMXVnaVdnV2M3SmZ2dzBQMEtiQ2pKQzJYMXJUTUZweUp0TUR0NUxTeU52XG5HK29lVkhVQ2ZZMXZEODhYWEx0Q2JQUTJQM1Y1c21mWHVhUEdzZ2l4N3dLQmdRQzlvQWl6RnI2QXVhQVZxRG41XG5MTWVZb2Z1aTFnSnRvSzFGd2NQUnpWaDJWR0JCbi9rODRRYU1rbTZqcXZ2TWxNTzlrTFZ0TTVxQnVJMk5BWUNHXG5FVzg3ZkhSTEZPUHQyZkhqRkpvOGMzb01jUTgvR3hsWVVwcmw3eDFNcFdBcWVmZ2RwV0d0MnNFL0lYQjJQaU1uXG5IVDd3eWZST1BuejJIcE5Bd3pPMVNKNmM3d0tCZ1FEWnFGOUp3TUQvWTlFUU5rUnptekwrdTUyZzJZa2oxYUFkXG5kNGI3UStFNG9NM1FwWG5FV0lrZStubWo5RnZOeVQ2MzNvSUZ1dXI5UU5sMC9nK1o2cmRYcit5aVRZWStJeWRZXG5ZQ09RZ0hDampsUGNJUnhzcjhQNFhZYW5MTytrNmxXdGNWYVFZeGtRckZJQi9pcnlKWUZMVXV4MWg5aEx0QjU1XG41TEFJVkxvbFd3S0JnSFNMMzh6eXRiTzRZMFBzV1hWa3FHaVBabkgyeHhkS0lqVTFtbjFna0hacDlaOWdDM2YzXG5kbWIvdVRDdTdvUVhDeGQvMFlLRXJQbjl6RUpXSWdCRXh2RHcxU2t3ZGNiQ3BZREVNT2Ztc2FUaitGTDQvT0xrXG5Jb0FnTHdua3V1NWdlTGIvNXNGTUttbjZ5M3hoUkluTjE5bDJ4c2t3YnUxMmJWcm9udWRVOHVmUkFvR0FaWU9YXG4zYWdlMjM1TWRZazU1OGxNd1czem1KK2N6Sy9IMjdaeHFDTUNtZ3kxc1VoY1FHMGlsdVl2Y1g1NGlzUC9rblllXG5vbG1KVzlDeEcxV2JKU2hKbG9ZZ2tab2lzRHhwRDFqWGZYL0tDUjVLa25LRzlWSU16bnVnWDd3ZmtRVVNMTENkXG50UDNqSk5tWjdnQURPNml1WXRrc0c2emQvb0RsYmVoeENaR3F0OTBDZ1lFQWlpQ0ZQbXJudm8zWHdWc2ErL1hvXG4rVi9GZHI0Q3NHNjFYd3FKVFFWZEdrMWZRbFl1a05vUWZvbjNTRENtVTZwb2hpUFRaRTVkSHFSWXk0L1BLUWFyXG5LQWJmK1lyc0hvT0E0bGlXQzNKYmM4emFKQnZOWDJMRENIRElxZVM1K1UvZTdJMWZKaGw2Mmd3cFhjYm1wL3BtXG5NT29UMUJUV3JNM2kwNURQenk1RUY4RT1cbi0tLS0tRU5EIFBSSVZBVEUgS0VZLS0tLS1cbiIKfQ==
```

## 🚀 How to Add Secrets to GitHub

### Step 1: Navigate to Repository Settings
1. Go to your GitHub repository: `https://github.com/YOUR_USERNAME/YOUR_REPO`
2. Click on **"Settings"** tab
3. In the left sidebar, click **"Secrets and variables"** → **"Actions"**

### Step 2: Add Each Secret
For each secret above:

1. Click **"New repository secret"**
2. **Name**: Enter the exact secret name (e.g., `YC_CLOUD_ID`)
3. **Secret**: Copy and paste the corresponding value
4. Click **"Add secret"**

### Step 3: Verify Secrets Added
You should see these 3 secrets in your repository:
- ✅ `YC_CLOUD_ID`
- ✅ `YC_FOLDER_ID`
- ✅ `YC_SERVICE_ACCOUNT_KEY`

## 🔧 Service Account Details

**Created Service Account:**
- **Name**: `github-actions-sa`
- **ID**: `aje1ojmlgr7rem5oegih`
- **Roles**:
  - `container-registry.images.pusher` - Push Docker images
  - `k8s.editor` - Deploy to Kubernetes
  - `compute.viewer` - View compute resources

## ✅ Test Your Setup

After adding secrets, test the pipeline:

### 1. Push to Dev Branch (Build Only)
```bash
git checkout dev
echo "# Test CI $(date)" >> README.md
git add . && git commit -m "test: CI pipeline" && git push origin dev
```

### 2. Push to Master Branch (Build + Deploy)
```bash
git checkout master
echo "# Test Deploy $(date)" >> README.md
git add . && git commit -m "deploy: test production deployment" && git push origin master
```

## 🔍 Monitoring

### GitHub Actions
- Go to **"Actions"** tab in your repository
- Watch the workflow runs and check for any errors

### Yandex Cloud
```bash
# Check if images are pushed
yc container image list --registry-id crpqt390b8gk59ipqid8

# Check Kubernetes deployments
kubectl get deployments -n cvetochey
kubectl get pods -n cvetochey
```

## 🚨 Security Notes

- **Never commit** the `github-actions-key.json` file to git
- The service account key is **base64 encoded** for GitHub secrets
- These secrets are **encrypted** and only accessible to GitHub Actions
- You can **regenerate** the service account key if needed

## 🔄 Key Regeneration (if needed)

If you need to regenerate the service account key:

```bash
# Delete old key
yc iam key delete <old-key-id>

# Create new key
yc iam key create --service-account-id aje1ojmlgr7rem5oegih --output new-key.json

# Encode for GitHub
base64 -i new-key.json

# Update GitHub secret with new value
```

---

🎯 **Ready to Deploy!** Your CI/CD pipeline is now configured to automatically build and deploy your application when you push changes to the `master` branch!


