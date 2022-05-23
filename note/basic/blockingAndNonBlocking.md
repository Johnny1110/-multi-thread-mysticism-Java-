# 阻塞 (Blocking)、非阻塞 (Non-Blocking)

<br>

---

<br>

## 阻塞 (Blocking)、非阻塞 (Non-Blocking) 解釋

<br>

阻塞與非阻塞用來形容多執行緒間互相影響。比如一個 Thread 佔用了一個共享資源 A，那麼其他 Thread 想要使用 A 就都必須等待，等待就會造成 __阻塞__ ，如果 A 一直不被釋放那其他 Thread 就永遠無法繼續工作。

__非阻塞__ 強調沒有一個 Thread 可以妨礙其他 Thread 執行工作，所有 Thread 都會不斷嘗試執行。