# Form validation

Adapted from OpenDesign's `craft/form-validation.md` (Apache-2.0).

- **Validate on blur, not on keystroke.** Typing should never feel like an error.
- After the first submit attempt, switch to validate-on-change for that field only.
- Error message: what went wrong + how to fix, in the interface's voice. Never apologise,
  never vague ("Invalid input").
- Place the message under the field, tied with an icon and text (never colour alone).
- Preserve input on every failure, including server rejections.
- Disable the submit button only when the form is genuinely incomplete; otherwise let it
  submit and show precise errors (a disabled button with no reason is a dead end).
- Show password rules before typing, not after failing.
- Loading state on submit keeps the button width and blocks double submission.
- Success: confirm inline for a moment, then move on. No modal for a completed form.
