# CJMP Review Follow-ups

Updated: 2026-03-17

## Open S3 follow-ups after PR #4 merge

- [ ] Keep the latest sent preview authoritative after S2 refresh.
  Scope: `apps/cjmp/telegram_compare_app/lib/chat_list.cj`
  Goal: after refreshing the chat list, sending in `Telegram Compare`, and returning to S2, the latest preview and time still come from the successful send instead of the synthetic refresh text.

- [ ] Prevent a second send from stranding an earlier message in `sending`.
  Scope: `apps/cjmp/telegram_compare_app/lib/index.cj`
  Goal: either block a second send while `pendingSendId` is active or expand the state model so multiple in-flight sends can resolve cleanly.

- [ ] Replace the fixed 14-slot message renderer with data-driven detail rendering.
  Scope: `apps/cjmp/telegram_compare_app/lib/index.cj`
  Goal: appended messages remain visible past the third post-fixture send, and `failed` / `retrying` affordances still render for later rows.

- [ ] Reopen a thread with the same in-session message mutations.
  Scope: `apps/cjmp/telegram_compare_app/lib/chat_detail.cj`
  Goal: backing out to S2 and reopening the same chat should preserve sent, failed, and retried messages so detail state matches the updated list preview.

- [ ] Re-capture CJMP S3 device evidence after the code follow-ups land.
  Scope: `apps/cjmp/docs/s3-chat-detail-and-send-plan.md`
  Goal: produce clean evidence for `loading`, `ready`, `send success`, `failed`, `retrying`, and `return-to-list preview writeback`, then update the parity matrix and AI delivery log.
