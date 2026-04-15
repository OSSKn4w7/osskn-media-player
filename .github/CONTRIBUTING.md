# Contributing to osskn播放器

Thank you for considering contributing to osskn播放器! 

## Development Setup

1. Fork and clone the repository
2. Open in Android Studio Hedgehog or later
3. Make sure you have JDK 17 installed

## Pull Request Process

1. Ensure any install or build dependencies are removed before the end of the layer when doing a 
   build.
2. Update the README.md with details of changes to the interface, this includes new environment 
   variables, exposed ports, useful file locations and container parameters.
3. Increase the version numbers in any examples files and the README.md to the new version that this
   Pull Request would represent. The versioning scheme we use is [SemVer](http://semver.org/).
4. You may merge the Pull Request in once you have the sign-off of two other developers, or if you 
   do not have permission to do that, you may request the second reviewer to merge it for you.

## CI/CD Pipeline

Our CI/CD pipeline automatically runs the following checks on every pull request:

- Unit tests
- Lint checks
- Build verification
- Instrumented tests (on emulator)

To trigger a release build, maintainers can manually run the "Build Release APK" workflow.

## Signing Configuration

For release builds, the following secrets must be configured in the repository:

- `SIGNING_KEY`: Base64-encoded keystore file
- `KEYSTORE_PASSWORD`: Password for the keystore
- `KEY_ALIAS`: Alias of the signing key
- `KEY_PASSWORD`: Password for the signing key