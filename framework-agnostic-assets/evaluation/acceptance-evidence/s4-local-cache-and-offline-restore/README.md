# S4 Acceptance Evidence

- Slice: `S4 本地缓存与离线恢复`
- Track: `KMP`
- Date: `2026-03-15`

## Files

- `s4-restore-list.png` / `s4-restore-list.xml`
  - 冷启动后直接恢复列表态，并展示缓存恢复 banner
- `s4-restore-detail.png` / `s4-restore-detail.xml`
  - 冷启动后直接恢复到 `Telegram Compare` 详情页，并展示缓存恢复 banner
- `s4-restore-detail-latest-message.png` / `s4-restore-detail-latest-message.xml`
  - 冷启动恢复后继续下滚，可看到本轮新发消息 `S4CACHE7kk`
- `s4-cache-cleared-fallback.png` / `s4-cache-cleared-fallback.xml`
  - 在详情页清空缓存后冷启动，回到正常列表加载路径，不再恢复旧详情
- `s4-logout-clears-cache.png` / `s4-logout-clears-cache.xml`
  - logout 后冷启动回到登录卡片，不恢复旧聊天上下文
