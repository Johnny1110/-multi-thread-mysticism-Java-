# 甚麼時候用並行 (Parallel) ?

---

<br>

---

<br>

__Linus Torvalds__  對並行是這麼評價的：

>"Where the hell do you envision that those magical parallel algorithms would be used?"

>"The only place where parallelism matters is in graphics or on the server side, where we already largely have it. Pushing it anywhere else is just pointless."

<br>

除了在圖形運算與伺服器方面廣泛大量使用，其他地方毫無必要使用多執行緒。這是 Lniux 之父對多執行緒的評價。

多執行緒在編寫以及維護方面比串行邏輯較困難，所以能不要用就不要用，除非遇到特定兩種 case：

<br>

* __圖形運算__

* __伺服器開發__

<br>

讓多個執行緒有效正確工作是一門藝術，保證 Thread safe，理解 multi-thread 的無序性、可見性，不使用鎖改用 CAS (Compare And Swap) 機制提高性能。這些都是這個筆記的重點研究方向。