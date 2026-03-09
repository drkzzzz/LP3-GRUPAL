import { defineConfig } from 'vite'
import react from '@vitejs/plugin-react'
import path from 'path'

// https://vite.dev/config/
export default defineConfig({
  plugins: [react()],
  resolve: {
    alias: {
      '@': path.resolve(__dirname, './src'),
    },
  },
  server: {
    port: 3000,
    proxy: {
      '/restful': {
        target: 'http://licores.spring.informaticapp.com:6677',
        changeOrigin: true,
      },
      '/uploads': {
        target: 'http://licores.spring.informaticapp.com:6677',
        changeOrigin: true,
      },
    },
  },
})
