# BotGPT

对接OpenAI ChatGPT API提供私聊、群聊AI对话的Mirai Console插件(QQ机器人)
### features
[x] 可配置连续对话、速率限制、最大字符限制

- [x] 可配置http、socks代理

- [x] 分段消息合并转发

- [] 代码不分段

- [] LaTex公式自动渲染

---

自行准备OpenAI token

权限:cn.mpamc.botgpt:botgpt.chat(可给予用户或群聊)

群聊@机器人 [要提问的内容] 或 清空会话

私聊直接发送[要提问的内容] 或 清空会话

发送 清空会话 在过期时间内清除机器人调用API时发送的历史消息

