/**
 * Client-side validation rules — keep in sync with backend ValidationPatterns and DTO constraints.
 */
const ValidationRules = {
  MIN_PASSWORD: 8,
  MAX_PASSWORD: 128,
  MIN_COMMITMENT: 4500000,
  MAX_REASON: 500,
  MAX_BIO: 2000,
  MAX_FIRM: 120,

  PATTERNS: {
    EMAIL: /^[^\s@]+@[^\s@]+\.[^\s@]+$/,
    FUND_ID: /^fund_[a-z_]+$/,
    ACCOUNT_ID: /^acc_\d{3}$/,
    PHONE: /^\+?[0-9\s\-()]{7,30}$/,
    OPTIONAL_PHONE: /^$|^\+?[0-9\s\-()]{7,30}$/,
    PERSON_NAME: /^[\p{L}\p{M}'\-. ]{1,80}$/u,
    OPTIONAL_PERSON_NAME: /^$|^[\p{L}\p{M}'\-. ]{1,80}$/u
  },

  /** @returns {{ valid: boolean, errors: Record<string,string>, message: string }} */
  validateLogin({ email, password }) {
    const errors = {};
    if (!email?.trim()) errors.email = 'Email is required';
    else if (!this.PATTERNS.EMAIL.test(email.trim())) errors.email = 'Enter a valid email address';
    if (!password) errors.password = 'Password is required';
    else if (password.length > this.MAX_PASSWORD) errors.password = `Password must be at most ${this.MAX_PASSWORD} characters`;
    return this._result(errors);
  },

  validateRegister({ email, password, firstName, lastName, firmName, dateOfBirth }) {
    const errors = {};
    if (!email?.trim()) errors.email = 'Email is required';
    else if (!this.PATTERNS.EMAIL.test(email.trim())) errors.email = 'Enter a valid email address';
    if (!password) errors.password = 'Password is required';
    else if (password.length < this.MIN_PASSWORD) errors.password = `Password must be at least ${this.MIN_PASSWORD} characters`;
    else if (password.length > this.MAX_PASSWORD) errors.password = `Password must be at most ${this.MAX_PASSWORD} characters`;
    if (!firstName?.trim()) errors.firstName = 'First name is required';
    else if (!this.PATTERNS.PERSON_NAME.test(firstName.trim())) errors.firstName = 'Invalid first name';
    if (!lastName?.trim()) errors.lastName = 'Last name is required';
    else if (!this.PATTERNS.PERSON_NAME.test(lastName.trim())) errors.lastName = 'Invalid last name';
    if (firmName && firmName.length > this.MAX_FIRM) errors.firmName = `Firm name must be at most ${this.MAX_FIRM} characters`;
    if (!dateOfBirth) errors.dateOfBirth = 'Date of birth is required';
    else if (!this._isPastDate(dateOfBirth)) errors.dateOfBirth = 'Date of birth must be in the past';
    return this._result(errors);
  },

  validateProfile({ firstName, lastName, phone, firmName, bio }) {
    const errors = {};
    if (firstName && !this.PATTERNS.OPTIONAL_PERSON_NAME.test(firstName.trim())) {
      errors.firstName = 'Invalid first name';
    }
    if (lastName && !this.PATTERNS.OPTIONAL_PERSON_NAME.test(lastName.trim())) {
      errors.lastName = 'Invalid last name';
    }
    if (phone != null && phone !== '' && !this.PATTERNS.OPTIONAL_PHONE.test(phone.trim())) {
      errors.phone = 'Invalid phone number';
    }
    if (firmName && firmName.length > this.MAX_FIRM) errors.firmName = `Firm name must be at most ${this.MAX_FIRM} characters`;
    if (bio && bio.length > this.MAX_BIO) errors.bio = `Bio must be at most ${this.MAX_BIO} characters`;
    return this._result(errors);
  },

  validateWithdrawal({ amount, accountId, type, reason }, balanceData) {
    const errors = {};
    if (amount == null || isNaN(amount) || amount <= 0) {
      errors.amount = 'Amount must be greater than zero';
    }
    if (!accountId) errors.accountId = 'Account is required';
    else if (!this.PATTERNS.ACCOUNT_ID.test(accountId)) errors.accountId = 'Invalid account ID';
    if (!type) errors.type = 'Withdrawal type is required';
    if (reason && reason.length > this.MAX_REASON) {
      errors.reason = `Reason must be at most ${this.MAX_REASON} characters`;
    }
    if (!errors.amount && balanceData) {
      if (amount > balanceData.availableBalance) {
        errors.amount = 'Amount exceeds available balance of ' + fmtFull(balanceData.availableBalance);
      } else if (amount > balanceData.maxWithdrawalAmount) {
        errors.amount = 'Amount exceeds the 90% withdrawal limit of ' + fmtFull(balanceData.maxWithdrawalAmount);
      }
      if (type === 'RETIREMENT' && !balanceData.retirementEligible) {
        errors.type = 'Retirement withdrawals are only available for investors over age 65';
      }
    }
    return this._result(errors);
  },

  validateInvestment({ fundId, amount, accountId, fundingDate, accreditedInvestor, termsAccepted, digitalSignature, userFullName }) {
    const errors = {};
    if (!fundId) errors.fundId = 'Please select a fund to invest in';
    else if (!this.PATTERNS.FUND_ID.test(fundId)) errors.fundId = 'Invalid fund ID';
    if (amount == null || isNaN(amount) || amount < this.MIN_COMMITMENT) {
      errors.amount = `Minimum commitment is ${fmt(this.MIN_COMMITMENT)}`;
    }
    if (!accountId) errors.accountId = 'Please select a funding source';
    else if (!this.PATTERNS.ACCOUNT_ID.test(accountId)) errors.accountId = 'Invalid account ID';
    if (!fundingDate) errors.fundingDate = 'Please select an intended funding date';
    else if (!this._isTodayOrFuture(fundingDate)) errors.fundingDate = 'Funding date cannot be in the past';
    if (!accreditedInvestor) errors.accreditedInvestor = 'Accredited investor confirmation is required';
    if (!termsAccepted) errors.termsAccepted = 'Terms and conditions must be accepted';
    if (!digitalSignature?.trim()) {
      errors.digitalSignature = 'Digital signature is required';
    } else if (digitalSignature.trim().length < 2) {
      errors.digitalSignature = 'Digital signature is required';
    } else if (userFullName && digitalSignature.trim().toLowerCase() !== userFullName.toLowerCase()) {
      errors.digitalSignature = 'Digital signature must match your full legal name on file';
    }
    return this._result(errors);
  },

  _isPastDate(isoDate) {
    const d = new Date(isoDate + 'T00:00:00');
    const today = new Date();
    today.setHours(0, 0, 0, 0);
    return d < today;
  },

  _isTodayOrFuture(isoDate) {
    const d = new Date(isoDate + 'T00:00:00');
    const today = new Date();
    today.setHours(0, 0, 0, 0);
    return d >= today;
  },

  _result(errors) {
    const keys = Object.keys(errors);
    return {
      valid: keys.length === 0,
      errors,
      message: keys.length ? Object.values(errors)[0] : ''
    };
  }
};

/** Maps backend field names to form input IDs per form context. */
const ServerFieldMaps = {
  auth: {
    email: 'email',
    password: 'password',
    firstName: 'first-name',
    lastName: 'last-name',
    firmName: 'firm-name',
    dateOfBirth: 'date-of-birth'
  },
  profile: {
    firstName: 'field-fullname',
    lastName: 'field-fullname',
    phone: 'field-phone',
    firmName: 'field-firm',
    bio: 'field-bio'
  },
  withdrawal: {
    amount: 'wd-amount',
    accountId: 'wd-account',
    type: 'wd-type',
    reason: 'wd-reason'
  },
  investment: {
    fundId: 'fund-selection',
    amount: 'inv-amount',
    accountId: 'inv-account',
    fundingDate: 'inv-date',
    accreditedInvestor: 'chk-accredited',
    termsAccepted: 'chk-terms',
    digitalSignature: 'signature-name'
  }
};

/**
 * Parse backend ErrorResponse.message like "email: invalid; password: required".
 * @returns {{ message: string, fieldErrors: Record<string,string> }}
 */
function parseServerValidationMessage(message) {
  if (!message) return { message: '', fieldErrors: {} };

  const fieldErrors = {};
  const general = [];

  message.split(';').forEach(part => {
    const trimmed = part.trim();
    if (!trimmed) return;
    const colon = trimmed.indexOf(': ');
    if (colon > 0) {
      const key = trimmed.slice(0, colon).trim();
      fieldErrors[key] = trimmed.slice(colon + 2).trim();
    } else {
      general.push(trimmed);
    }
  });

  const mappedKeys = Object.keys(fieldErrors);
  const summary = mappedKeys.length
    ? (general.length ? general.join('; ') : Object.values(fieldErrors)[0])
    : message;

  return { message: summary, fieldErrors };
}

function _fieldContainer(input) {
  return input.closest('.auth-form-group, .form-group, .profile-field, .investment-field, .legal-check, .signature-box, .input-prefix');
}

function clearFieldErrors(scope) {
  const root = typeof scope === 'string' ? document.getElementById(scope) : (scope || document);
  if (!root) return;
  root.querySelectorAll('.field-error').forEach(el => {
    el.textContent = '';
    el.style.display = 'none';
  });
  root.querySelectorAll('.input-invalid, .check-invalid').forEach(el => {
    el.classList.remove('input-invalid', 'check-invalid');
  });
}

function setFieldError(inputId, message) {
  const input = document.getElementById(inputId);
  if (!input) return;

  const isCheckbox = input.type === 'checkbox';
  input.classList.toggle(isCheckbox ? 'check-invalid' : 'input-invalid', !!message);

  const container = _fieldContainer(input) || input.parentElement;
  if (!container) return;

  let err = container.querySelector(`.field-error[data-for="${inputId}"]`);
  if (!err) {
    err = document.createElement('span');
    err.className = 'field-error';
    err.setAttribute('data-for', inputId);
    container.appendChild(err);
  }
  err.textContent = message || '';
  err.style.display = message ? 'block' : 'none';
}

function setFundSelectionError(message) {
  let err = document.getElementById('fund-selection-error');
  if (!err) {
    const section = document.querySelector('.investment-section .fund-grid')?.parentElement;
    if (!section) return;
    err = document.createElement('p');
    err.id = 'fund-selection-error';
    err.className = 'field-error fund-selection-error';
    section.querySelector('.fund-grid')?.insertAdjacentElement('afterend', err);
  }
  err.textContent = message || '';
  err.style.display = message ? 'block' : 'none';
}

/** Apply field-level errors only (no form banner). */
function applyFieldErrors(formKey, fieldErrors) {
  const map = ServerFieldMaps[formKey] || {};
  const seenUi = new Set();

  Object.entries(fieldErrors).forEach(([serverKey, msg]) => {
    if (serverKey === 'fundId' || map[serverKey] === 'fund-selection') {
      setFundSelectionError(msg);
      return;
    }
    const uiId = map[serverKey];
    if (uiId && !seenUi.has(uiId)) {
      setFieldError(uiId, msg);
      seenUi.add(uiId);
    }
  });
}

function showFormError(elId, msg) {
  const el = document.getElementById(elId);
  if (!el) return;
  el.textContent = msg || '';
  el.style.display = msg ? 'block' : 'none';
}

function _formScope(formErrorId) {
  return document.getElementById(formErrorId)?.closest(
    'form, .auth-card, .page-content, .investment-content, .profile-content'
  ) || document.body;
}

function applyValidationResult(formKey, result, formErrorId) {
  clearFieldErrors(_formScope(formErrorId));
  if (result.valid) {
    if (formErrorId) showFormError(formErrorId, '');
    return true;
  }
  applyFieldErrors(formKey, result.errors);
  if (formErrorId) showFormError(formErrorId, result.message);
  return false;
}

function applyServerErrors(formKey, serverMessage, formErrorId) {
  const { message, fieldErrors } = parseServerValidationMessage(serverMessage);
  clearFieldErrors(_formScope(formErrorId));
  if (Object.keys(fieldErrors).length) applyFieldErrors(formKey, fieldErrors);
  if (formErrorId) showFormError(formErrorId, message);
  return message;
}

/** Apply errors from apiPost/apiPut throws (fieldErrors + message). */
function applyApiError(formKey, err, formErrorId) {
  clearFieldErrors(_formScope(formErrorId));
  const fields = err.fieldErrors && Object.keys(err.fieldErrors).length
    ? err.fieldErrors
    : parseServerValidationMessage(err.message).fieldErrors;
  if (Object.keys(fields).length) applyFieldErrors(formKey, fields);
  if (formErrorId) showFormError(formErrorId, err.message);
}

function wireFieldClearOnInput(formRoot, inputIds) {
  inputIds.forEach(id => {
    const el = document.getElementById(id);
    if (!el) return;
    const evt = el.type === 'checkbox' || el.tagName === 'SELECT' ? 'change' : 'input';
    el.addEventListener(evt, () => {
      setFieldError(id, '');
      if (id === 'field-fullname') {
        ['firstName', 'lastName'].forEach(() => setFieldError('field-fullname', ''));
      }
    });
  });
}
