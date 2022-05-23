# 死鎖 (Deadlock)、飢餓 (Starvation)、活鎖 (Livelock)

<br>

---

<br>

## 死鎖 (Deadlock)、飢餓 (Starvation)、活鎖 (Livelock) 解釋

<br>

### 死鎖 (Deadlock)：

<br>

死鎖問題，靠北工程師上有一個笑話很好解釋這個問題 PG 與 PM 的日常對話 :

PG : "你先給我需求，我再寫 code。"

PM : "你先寫 code，我再給你需求。"

大家互不讓步，然後就這樣乾瞪眼等下班... 這就是死鎖，正經解釋一下就是兩個 Thread 都需要 A 與 B 兩個物件的使用權才能執行任務，Thread-1 持有 A，Thread-2 則持有 B，然後兩個 Thread 就互相等待對方退出資源競爭，然後就等到天荒地老。

<br>
<br>

### 飢餓 (Starvation)：

<br>



<br>
<br>

### 活鎖 (Livelock)：

