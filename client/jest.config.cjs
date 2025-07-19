module.exports = {
  testEnvironment: 'jsdom',
  setupFilesAfterEnv: ['./setupTests.ts'],
  moduleNameMapper: {
    '^@/(.*)$': '<rootDir>/src/$1',
    'react-markdown': '<rootDir>/src/__mocks__/react-markdown.tsx',
    'remark-gfm': '<rootDir>/src/__mocks__/remark-gfm.tsx',
  },
}
