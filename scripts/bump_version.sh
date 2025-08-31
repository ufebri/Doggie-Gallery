#!/bin/bash

set -e

FILE="version.properties"
LAST_COMMIT=$(git log -1 --pretty=%B)

LEVEL="patch"  # default

if echo "$LAST_COMMIT" | grep -q "BREAKING CHANGE"; then
  LEVEL="major"
elif echo "$LAST_COMMIT" | grep -q "^feat"; then
  LEVEL="minor"
elif echo "$LAST_COMMIT" | grep -q "^fix"; then
  LEVEL="patch"
fi

echo "🔍 Commit analyzed: '$LAST_COMMIT'"
echo "🔧 Auto bump level: $LEVEL"

VERSION_CODE=$(grep VERSION_CODE $FILE | cut -d'=' -f2)
VERSION_NAME=$(grep VERSION_NAME $FILE | cut -d'=' -f2)

IFS='.' read -r MAJOR MINOR PATCH <<< "$VERSION_NAME"

case $LEVEL in
  patch)
    PATCH=$((PATCH + 1))
    ;;
  minor)
    MINOR=$((MINOR + 1))
    PATCH=0
    ;;
  major)
    MAJOR=$((MAJOR + 1))
    MINOR=0
    PATCH=0
    ;;
esac

NEW_VERSION_NAME="$MAJOR.$MINOR.$PATCH"
NEW_VERSION_CODE=$((VERSION_CODE + 1))

echo "VERSION_CODE=$NEW_VERSION_CODE" > $FILE
echo "VERSION_NAME=$NEW_VERSION_NAME" >> $FILE

echo "✅ Version bumped to $NEW_VERSION_CODE ($NEW_VERSION_NAME)"