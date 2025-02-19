FROM node:22.13

ENV NODE_OPTIONS="--max-old-space-size=8192"

WORKDIR /app

COPY package*.json ./

RUN npm install --save-dev @rollup/rollup-linux-x64-gnu
RUN npm install

COPY . .

RUN npm run build

ENV NODE_ENV=production

EXPOSE 3000

ENTRYPOINT ["npm", "start"]
