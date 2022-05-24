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

飢餓指 Thread 因某些原因遲遲無法取得所需資源，而導致一直無法執行任務，比如優先級太低，被高優先級 Thread 一直插隊，或者因為某個 Thread 一直持有資源不放，導致其他人等不到資源。

<br>
<br>

### 活鎖 (Livelock)：

<br>

活鎖就是 Thread 之間互相謙讓而造成的問題，就像是死鎖情況下 Thread-1 與 Thread-2 同時釋放所持有資源讓給對方，然後兩個 Thread 就都無法執行任務了。這種情況就會出現資源不斷在兩個 Thread 中輪轉但沒有一個 Thread 可以同時持有 2 個可執行任務的資源。

