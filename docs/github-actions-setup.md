# GitHub Actions Setup Guide

This document explains how to configure secrets for the GitHub Actions release workflow.

## Setting Up Signing Secrets

To enable the release workflow in GitHub Actions, you need to configure these secrets in your repository settings:

1. Encode your keystore file to base64:
```bash
base64 -i osskn4w7.keystore | tr -d '\n'
```

2. Add the following secrets to your repository:
   - `SIGNING_KEY`: The base64-encoded keystore content
   - `KEYSTORE_PASSWORD`: The password for the keystore (`osskn4w7`)
   - `KEY_ALIAS`: The alias name (`osskn4w7`)
   - `KEY_PASSWORD`: The password for the signing key (`osskn4w7`)

## Repository Secrets Setup

Go to your repository Settings > Secrets and variables > Actions and add:

```
SIGNING_KEY = [base64 encoded keystore content]
KEYSTORE_PASSWORD = osskn4w7
KEY_ALIAS = osskn4w7
KEY_PASSWORD = osskn4w7
```

## Alternative Keystore Configuration

For production releases, you might want to use a different keystore. To do this:

1. Replace the `osskn4w7.keystore` file with your own
2. Update the `app/build.gradle` file with your new keystore details
3. Update the secrets accordingly
``