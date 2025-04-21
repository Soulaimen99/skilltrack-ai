import { useState, useEffect } from "react";
import { useKeycloak } from "@react-keycloak/web";
import DateFilter from "../components/DateFilter";
import TagFilter from "../components/TagFilter";
import LogList from "../components/LogList";
import SummaryBox from "../components/SummaryBox";
import { isDateInRange } from "../utils/dateUtils";

export default function Dashboard() {
  const { keycloak } = useKeycloak();
  const [allLogs, setAllLogs] = useState([]);
  const [logs, setLogs] = useState([]);
  const [content, setContent] = useState("");
  const [tags, setTags] = useState("");
  const [summary, setSummary] = useState("");
  const [remaining, setRemaining] = useState("");
  const [loadingLogs, setLoadingLogs] = useState(false);
  const [loadingSummary, setLoadingSummary] = useState(false);
  const [activeTag, setActiveTag] = useState(null);
  const [presetRange, setPresetRange] = useState("all");
  const [dateRange, setDateRange] = useState("");
  const [page, setPage] = useState(0);
  const [hasMore, setHasMore] = useState(true);

  const allTags = Array.from(
    new Set(
      allLogs.flatMap((log) => log.tags?.split(",").map((t) => t.trim()) || [])
    )
  ).filter((tag) => tag);

  useEffect(() => {
    if (keycloak.authenticated) fetchLogs();
  }, [keycloak, page]);

  useEffect(() => {
    const filtered = allLogs.filter((log) => {
      const matchesTag =
        !activeTag ||
        (log.tags || "")
          .split(",")
          .map((t) => t.trim())
          .includes(activeTag);
      const inDateRange = isDateInRange(log.date, dateRange.from, dateRange.to);
      return matchesTag && inDateRange;
    });
    setLogs(filtered);
  }, [activeTag, dateRange, allLogs]);

  useEffect(() => {
    setPage(0);
    setHasMore(true);
  }, [activeTag, dateRange]);

  const fetchLogs = async () => {
    setLoadingLogs(true);
    try {
      const params = new URLSearchParams();
      if (dateRange.from) params.append("from", dateRange.from);
      if (dateRange.to) params.append("to", dateRange.to);
      params.append("page", page);
      params.append("size", 2);

      const res = await fetch(`/logs?${params.toString()}`, {
        headers: { Authorization: `Bearer ${keycloak.token}` },
      });

      if (!res.ok) throw new Error(await res.text());
      const result = await res.json();

      const { content, totalPages } = result;
      setAllLogs((prev) => (page === 0 ? content : [...prev, ...content]));
      setHasMore(page + 1 < totalPages);
    } catch (err) {
      console.error("Failed to fetch logs:", err);
    } finally {
      setLoadingLogs(false);
    }
  };

  const handleAddLog = async (e) => {
    e.preventDefault();
    try {
      const res = await fetch("/logs", {
        method: "POST",
        headers: {
          "Content-Type": "application/json",
          Authorization: `Bearer ${keycloak.token}`,
        },
        body: JSON.stringify({ content, tags }),
      });

      if (!res.ok) throw new Error(await res.text());
      const newLog = await res.json();
      setAllLogs((prev) => [...prev, newLog]);
      setContent("");
      setTags("");
    } catch (err) {
      console.error("Error adding log:", err);
    }
  };

  const handleUpdate = async (id, content, tags) => {
    try {
      const res = await fetch(`/logs/${id}`, {
        method: "PUT",
        headers: {
          "Content-Type": "application/json",
          Authorization: `Bearer ${keycloak.token}`,
        },
        body: JSON.stringify({ content, tags }),
      });
      if (!res.ok) throw new Error(await res.text());
      const updatedLog = await res.json();
      setAllLogs((prev) =>
        prev.map((log) => (log.id === id ? updatedLog : log))
      );
    } catch (err) {
      console.error("Failed to update log:", err);
    }
  };

  const handleDelete = async (id) => {
    try {
      const res = await fetch(`/logs/${id}`, {
        method: "DELETE",
        headers: { Authorization: `Bearer ${keycloak.token}` },
      });
      if (res.ok) {
        setAllLogs((prev) => prev.filter((log) => log.id !== id));
      } else {
        console.error("Failed to delete log:", await res.text());
      }
    } catch (err) {
      console.error("Error deleting log:", err);
    }
  };

  const handleSummarize = async () => {
    setLoadingSummary(true);
    try {
      const res = await fetch("/logs/summarize", {
        method: "POST",
        headers: {
          "Content-Type": "application/json",
          Authorization: `Bearer ${keycloak.token}`,
        },
        body: JSON.stringify(logs),
      });
      const remaining = res.headers.get("X-RateLimit-Remaining");
      if (!res.ok) throw new Error(await res.text());
      const data = await res.json();
      setSummary(data);
      setRemaining(remaining);
    } catch (err) {
      console.error("Error generating summary:", err);
    } finally {
      setLoadingSummary(false);
    }
  };

  return (
    <div className="container">
      <TagFilter
        allTags={allTags}
        activeTag={activeTag}
        setActiveTag={setActiveTag}
      />
      <DateFilter
        dateRange={dateRange}
        setDateRange={setDateRange}
        presetRange={presetRange}
        setPresetRange={setPresetRange}
      />

      <h2>Learning Logs</h2>
      {loadingLogs ? (
        <p>Loading logs...</p>
      ) : (
        <LogList
          logs={logs}
          activeTag={activeTag}
          handleUpdate={handleUpdate}
          handleDelete={handleDelete}
        />
      )}

      {hasMore && !loadingLogs && (
        <button onClick={() => setPage((prev) => prev + 1)}>
          Load More Logs
        </button>
      )}

      <h3>Add New Log</h3>
      <form onSubmit={handleAddLog}>
        <textarea
          value={content}
          onChange={(e) => setContent(e.target.value)}
          placeholder="What did you learn?"
          rows="4"
          required
        />
        <input
          type="text"
          value={tags}
          onChange={(e) => setTags(e.target.value)}
          placeholder="Tags (comma separated)"
        />
        <button type="submit" disabled={content.length === 0}>
          Add Log
        </button>
      </form>

      <SummaryBox
        summary={summary}
        loadingSummary={loadingSummary}
        handleSummarize={handleSummarize}
        logs={logs}
        dateRange={dateRange}
        remaining={remaining}
      />
    </div>
  );
}
