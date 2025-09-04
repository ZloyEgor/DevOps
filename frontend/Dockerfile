# syntax=docker/dockerfile:1

# Multi-architecture support
ARG BUILDPLATFORM=linux/amd64
ARG TARGETPLATFORM=linux/amd64

# Base image with multi-arch support
FROM --platform=$BUILDPLATFORM node:20-alpine AS base

# Install dependencies only when needed
FROM base AS deps
# Check https://github.com/nodejs/docker-node/tree/b4117f9333da4138b03a546ec926ef50a31506c3#nodealpine to understand why libc6-compat might be needed.
RUN apk add --no-cache libc6-compat
WORKDIR /app

# Enable pnpm and configure registry BEFORE copying files
RUN corepack enable pnpm
RUN pnpm config set registry https://registry.npmjs.org/
RUN npm config set registry https://registry.npmjs.org/

# Set environment variables to force registry
ENV npm_config_registry=https://registry.npmjs.org/
ENV PNPM_REGISTRY=https://registry.npmjs.org/

# Install dependencies based on the preferred package manager
COPY package.json yarn.lock* package-lock.json* pnpm-lock.yaml* .npmrc* ./

RUN \
  if [ -f pnpm-lock.yaml ]; then pnpm i --frozen-lockfile --registry https://registry.npmjs.org/; \
  elif [ -f yarn.lock ]; then yarn --frozen-lockfile --registry https://registry.npmjs.org/; \
  elif [ -f package-lock.json ]; then npm ci --registry https://registry.npmjs.org/; \
  else echo "Lockfile not found." && exit 1; \
  fi


# Rebuild the source code only when needed
FROM base AS builder
WORKDIR /app
COPY --from=deps /app/node_modules ./node_modules
COPY . .

# Disable telemetry for build
ENV NEXT_TELEMETRY_DISABLED=1
# Skip linting during build for deployment
ENV ESLINT_NO_DEV_ERRORS=true
ENV NEXT_LINT=false
# Optimize build memory usage for limited Docker memory
ENV NODE_OPTIONS="--max_old_space_size=1536 --max_semi_space_size=64"
ENV NEXT_WEBPACK_USEPOLLING=false
ENV NEXT_WEBPACK_WATCHOPTIONS_IGNORED="node_modules"

# Enable pnpm for build stage and configure registry
RUN corepack enable pnpm
RUN pnpm config set registry https://registry.npmjs.org/

# Set environment variables to force registry
ENV npm_config_registry=https://registry.npmjs.org/
ENV PNPM_REGISTRY=https://registry.npmjs.org/

RUN \
  if [ -f pnpm-lock.yaml ]; then pnpm run build; \
  elif [ -f yarn.lock ]; then yarn run build; \
  elif [ -f package-lock.json ]; then npm run build; \
  else echo "Lockfile not found." && exit 1; \
  fi

# Production image, copy all the files and run next
FROM --platform=$TARGETPLATFORM node:20-alpine AS runner

# Install security updates and required packages
RUN apk update && \
    apk upgrade && \
    apk add --no-cache \
    dumb-init \
    curl && \
    rm -rf /var/cache/apk/*

WORKDIR /app

ENV NODE_ENV=production
ENV NEXT_TELEMETRY_DISABLED=1

# Create non-root user
RUN addgroup --system --gid 1001 nodejs
RUN adduser --system --uid 1001 nextjs

COPY --from=builder /app/public ./public

# Automatically leverage output traces to reduce image size
# https://nextjs.org/docs/advanced-features/output-file-tracing
COPY --from=builder --chown=nextjs:nodejs /app/.next/standalone ./
COPY --from=builder --chown=nextjs:nodejs /app/.next/static ./.next/static

USER nextjs

# Health check
HEALTHCHECK --interval=30s --timeout=3s --start-period=30s --retries=3 \
    CMD curl -f http://localhost:3000/api/health || curl -f http://localhost:3000/ || exit 1

EXPOSE 3000

ENV PORT=3000
ENV HOSTNAME="0.0.0.0"

# Use dumb-init for proper signal handling
ENTRYPOINT ["dumb-init", "--"]
CMD ["node", "server.js"]