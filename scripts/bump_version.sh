#!/usr/bin/env bash
set -euo pipefail

FILE="version.properties"

if [[ ! -f "$FILE" ]]; then
  echo "ERROR: $FILE not found"; exit 1
fi

# Ambil full message (subject+body)
LAST_COMMIT=$(git log -1 --pretty=%B)
echo "🔍 Commit analyzed:"
echo "-------------------"
echo "$LAST_COMMIT"
echo "-------------------"

# Normalisasi (lowercase untuk match prefix), tapi simpan juga aslinya
LAST_COMMIT_LC=$(printf "%s" "$LAST_COMMIT" | tr '[:upper:]' '[:lower:]')

LEVEL="patch"  # default

# 1) Major jika ada "breaking change:" (footer) ATAU ada '!' setelah type, contoh: feat!: xxx
if echo "$LAST_COMMIT_LC" | grep -qiE '^breaking change:' || \
   echo "$LAST_COMMIT"    | grep -qE '^(feat|fix|perf|refactor|docs|test|build|ci|chore)(\([^)]+\))?!:'; then
  LEVEL="major"
# 2) Minor jika diawali feat:
elif echo "$LAST_COMMIT_LC" | grep -qE '^feat(\([^)]+\))?:'; then
  LEVEL="minor"
# 3) Patch jika diawali fix:, perf:, refactor:
elif echo "$LAST_COMMIT_LC" | grep -qE '^(fix|perf|refactor)(\([^)]+\))?:'; then
  LEVEL="patch"
# 4) Tipe lain (docs/test/build/ci/chore) → tetap patch (atau no bump kalau mau)
elif echo "$LAST_COMMIT_LC" | grep -qE '^(docs|test|build|ci|chore)(\([^)]+\))?:'; then
  LEVEL="patch"
fi

echo "🔧 Auto bump level: $LEVEL"

# Baca version.properties
VERSION_CODE=$(grep -E '^VERSION_CODE=' "$FILE" | cut -d'=' -f2)
VERSION_NAME=$(grep -E '^VERSION_NAME=' "$FILE" | cut -d'=' -f2)

IFS='.' read -r MAJOR MINOR PATCH <<< "$VERSION_NAME"

case "$LEVEL" in
  major)
    MAJOR=$((MAJOR + 1)); MINOR=0; PATCH=0;;
  minor)
    MINOR=$((MINOR + 1)); PATCH=0;;
  patch|*)
    PATCH=$((PATCH + 1));;
esac

NEW_VERSION_NAME="$MAJOR.$MINOR.$PATCH"
NEW_VERSION_CODE=$(( VERSION_CODE + 1 ))

# Tulis ulang file (hindari duplikasi)
{
  echo "VERSION_CODE=$NEW_VERSION_CODE"
  echo "VERSION_NAME=$NEW_VERSION_NAME"
} > "$FILE"

echo "✅ Version bumped to $NEW_VERSION_CODE ($NEW_VERSION_NAME)"