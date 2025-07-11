module.exports = {
  testEnvironment: 'jsdom',
  setupFilesAfterEnv: ['./setupTests.ts'],
  moduleNameMapper: {
    '^@/(.*)$': '<rootDir>/src/$1',
  },
};
