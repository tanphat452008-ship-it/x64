#include <ThreadOptimizer/ThreadOptimizer.h>

static void setThreadAffinityMask(pid_t tid, uint32_t mask)
{
    syscall(__NR_sched_setaffinity, tid, sizeof(mask), &mask);
}

void ThreadOptimizer::Routine()
{
    const uint32_t mask = 0xff;
    while (true)
    {
        m_Mutex.lock();

        for (auto& i : m_Threads)
        {
            setThreadAffinityMask(i, mask);
        }

        m_Mutex.unlock();

        std::this_thread::sleep_for(std::chrono::milliseconds(5000));
    }
}

ThreadOptimizer::ThreadOptimizer()
{
    std::thread(&ThreadOptimizer::Routine, this).detach();
}

ThreadOptimizer::~ThreadOptimizer()
{
}

void ThreadOptimizer::PushThread(pid_t tid)
{
    std::lock_guard<std::mutex> lock(m_Mutex);

    m_Threads.push_back(tid);
}