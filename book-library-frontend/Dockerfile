FROM node:22.15.0-alpine
WORKDIR /app
# Copy package.json and package-lock.json files first for better caching
COPY package*.json ./
RUN npm install
COPY . .
# Build the app for production (comment this out if you want dev mode)
# RUN npm run build
ENV HOST=0.0.0.0
EXPOSE 4200
CMD ["npm", "run", "start", "--", "--host", "0.0.0.0", "--disable-host-check"]
