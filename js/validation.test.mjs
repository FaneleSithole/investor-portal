import assert from 'node:assert/strict';
import { readFileSync } from 'node:fs';
import { dirname, join } from 'node:path';
import { fileURLToPath } from 'node:url';
import test from 'node:test';
import { createContext, runInContext } from 'node:vm';

const __dirname = dirname(fileURLToPath(import.meta.url));

function loadValidationRules() {
  const code = readFileSync(join(__dirname, 'validation.js'), 'utf8');
  const context = createContext({
    fmt: (n) => String(n),
    fmtFull: (n) => String(n),
    document: {},
  });
  runInContext(code + '\nthis.ValidationRules = ValidationRules;', context);
  return context.ValidationRules;
}

const ValidationRules = loadValidationRules();

test('validateLogin rejects empty email', () => {
  const result = ValidationRules.validateLogin({ email: '', password: 'secret' });
  assert.equal(result.valid, false);
  assert.equal(result.errors.email, 'Email is required');
});

test('validateLogin rejects invalid email format', () => {
  const result = ValidationRules.validateLogin({ email: 'not-an-email', password: 'secret' });
  assert.equal(result.valid, false);
  assert.match(result.errors.email, /valid email/i);
});

test('validateRegister rejects short password', () => {
  const result = ValidationRules.validateRegister({
    email: 'user@fanele.com',
    password: 'short',
    firstName: 'Thabo',
    lastName: 'Mokoena',
    firmName: '',
    dateOfBirth: '1985-06-15',
  });
  assert.equal(result.valid, false);
  assert.match(result.errors.password, /at least 8/i);
});

test('validateWithdrawal rejects amount above balance cap', () => {
  const result = ValidationRules.validateWithdrawal(
    { amount: 2100000, accountId: 'acc_001', type: 'STANDARD', reason: '' },
    { availableBalance: 2300000, maxWithdrawalAmount: 2070000, retirementEligible: false }
  );
  assert.equal(result.valid, false);
  assert.match(result.errors.amount, /90%/i);
});

test('validateInvestment rejects missing digital signature match', () => {
  const result = ValidationRules.validateInvestment({
    fundId: 'fund_pe',
    amount: 4500000,
    accountId: 'acc_001',
    fundingDate: '2030-01-01',
    accreditedInvestor: true,
    termsAccepted: true,
    digitalSignature: 'Wrong Name',
    userFullName: 'Thabo Mokoena',
  });
  assert.equal(result.valid, false);
  assert.match(result.errors.digitalSignature, /match your full legal name/i);
});

test('validateProfile rejects invalid phone', () => {
  const result = ValidationRules.validateProfile({
    firstName: 'Thabo',
    lastName: 'Mokoena',
    phone: 'not-a-phone',
    firmName: '',
    bio: '',
  });
  assert.equal(result.valid, false);
  assert.equal(result.errors.phone, 'Invalid phone number');
});
