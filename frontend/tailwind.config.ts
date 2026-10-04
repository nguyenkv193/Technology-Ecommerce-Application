import type { Config } from 'tailwindcss'

const config: Config = {
  content: [
    "./index.html",
    "./src/**/*.{vue,js,ts,jsx,tsx}",
  ],
  theme: {
    extend: {
      colors: {
        brand: {
          50: '#eff6ff',
          100: '#dbeafe',
          500: '#3b82f6',
          600: '#2563eb',
          700: '#1d4ed8',
          900: '#1e3a8a',
        },
        tech: {
          dark: '#0f172a',
          card: '#1e293b',
          accent: '#06b6d4',
          highlight: '#8b5cf6'
        }
      }
    },
  },
  plugins: [],
}

export default config
