---
name: atomic-commits
description: Create conventional atomic commits from local changes with appropriate commit messages based on change type and scope.
---

# Atomic Commits

Create clean, conventional commits that group related changes logically.

## When to Use

- You have multiple unrelated changes that should be separate commits
- You want to follow conventional commit standards (feat:, fix:, docs:, refactor:, etc.)
- You need clean git history for easier debugging, bisection, and changelog generation
- You're preparing code for review or merging to main branches

## Instructions

1. Stage your changes using `git add` to prepare what you want to commit
2. Run the atomic commit script: `scripts/atomic-commit.sh`
3. The script will:
   - Analyze staged changes to identify logical groupings
   - Group changes by type (features, fixes, refactors, docs, tests, chores)
   - Create conventional commit messages for each group
   - Ask for confirmation before committing
4. Review each proposed commit message and adjust if needed
5. Each commit will follow the conventional commit format: