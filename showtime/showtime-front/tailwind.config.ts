import type { Config } from 'tailwindcss'

export default {
  content: ['./index.html', './src/**/*.{vue,ts,tsx}'],
  theme: {
    extend: {
      fontFamily: {
        display: ['Space Grotesk', 'Pretendard', 'sans-serif'],
        body: ['Pretendard', 'Inter', 'sans-serif'],
      },
      colors: {
        court: {
          dark: '#0f1a22',
          line: '#f5f1e8',
          orange: '#e86d2f',
          mint: '#7be0c3',
        },
      },
      boxShadow: {
        card: '0 14px 40px rgba(10, 12, 18, 0.25)',
      },
    },
  },
  plugins: [],
} satisfies Config
