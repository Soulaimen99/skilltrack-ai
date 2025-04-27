import { useState, useEffect } from "react";
import { useKeycloak } from "@react-keycloak/web";
import LogList from "../components/LogList";
import TagFilter from "../components/TagFilter";
import DateFilter from "../components/DateFilter";

export default function LogsPage() {
  const { keycloak } = useKeycloak();
  const [logs, setLogs] = useState([]);
  const [content, setContent] = useState("");
  const [tags, setTags] = useState("");
  const [loadingLogs, setLoadingLogs] = useState(false);
  const [loadingExportLogs, setLoadingExportLogs] = useState(false);
  const [loadingExportSummaries, setLoadingExportSummaries] = useState(false);

  const [activeTag, setActiveTag] = useState(null);
  const [dateRange, setDateRange] = useState({ from: "", to: "" });
  const [presetRange, setPresetRange] = useState("all");

  useEffect(() => {
    if (keycloak.authenticated && keycloak.token) {
      fetchLogs();
    }
  }, [keycloak.authenticated, keycloak.token]);

  const fetchLogs = async () => {
    setLoadingLogs(true);
    try {
      const res = await fetch("/api/logs?page=0&size=100", {
        headers: { Authorization: `Bearer ${keycloak.token}` },
      });
      if (!res.ok) throw new Error(await res.text());
      const data = await res.json();
      setLogs(data.content || []);
    } catch (err) {
      console.error("Failed to fetch logs", err);
    } finally {
      setLoadingLogs(false);
    }
  };

  const handleAddLog = async (e) => {
    e.preventDefault();
    try {
      const logPayload = { content, tags };
      const res = await fetch("/api/logs", {
        method: "POST",
        headers: {
          "Content-Type": "application/json",
          Authorization: `Bearer ${keycloak.token}`,
        },
        body: JSON.stringify(logPayload),
      });
      if (!res.ok) throw new Error(await res.text());
      const newLog = await res.json();
      setLogs((prev) => [...prev, newLog]);
      setContent("");
      setTags("");
    } catch (err) {
      console.error("Failed to add log", err);
    }
  };

  const handleExportLogs = async (format = "json") => {
    setLoadingExportLogs(true);
    try {
      const res = await fetch(`/api/logs/export?format=${format}`, {
        headers: { Authorization: `Bearer ${keycloak.token}` },
      });
      if (!res.ok) throw new Error(await res.text());
      const blob = await res.blob();
      const url = URL.createObjectURL(blob);
      const a = document.createElement("a");
      a.href = url;
      a.download = `learning_logs.${format}`;
      a.click();
      URL.revokeObjectURL(url);
    } catch (err) {
      console.error("Failed to export logs:", err);
    } finally {
      setLoadingExportLogs(false);
    }
  };

  const handleExportSummaries = async (format = "json") => {
    setLoadingExportSummaries(true);
    try {
      const res = await fetch(`/api/summaries/export?format=${format}`, {
        headers: { Authorization: `Bearer ${keycloak.token}` },
      });
      if (!res.ok) throw new Error(await res.text());
      const blob = await res.blob();
      const url = URL.createObjectURL(blob);
      const a = document.createElement("a");
      a.href = url;
      a.download = `summaries.${format}`;
      a.click();
      URL.revokeObjectURL(url);
    } catch (err) {
      console.error("Failed to export summaries:", err);
    } finally {
      setLoadingExportSummaries(false);
    }
  };

  const uniqueTags = Array.from(
    new Set(
      logs.flatMap((log) =>
        (log.tags || "")
          .split(",")
          .map((tag) => tag.trim())
          .filter(Boolean)
      )
    )
  );

  const filteredLogs = logs.filter((log) => {
    const matchesTag =
      !activeTag ||
      (log.tags || "")
        .split(",")
        .map((t) => t.trim())
        .includes(activeTag);
    const matchesDate =
      (!dateRange.from || new Date(log.date) >= new Date(dateRange.from)) &&
      (!dateRange.to || new Date(log.date) <= new Date(dateRange.to));
    return matchesTag && matchesDate;
  });

  return (
    <div className="container">
      <h2>My Learning Logs</h2>

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
        <button type="submit">Add Log</button>
      </form>

      <div className="filters">
        <TagFilter
          allTags={uniqueTags}
          activeTag={activeTag}
          setActiveTag={setActiveTag}
        />
        <DateFilter
          dateRange={dateRange}
          setDateRange={setDateRange}
          presetRange={presetRange}
          setPresetRange={setPresetRange}
        />
      </div>

      {loadingLogs ? (
        <p>Loading logs...</p>
      ) : (
        <LogList logs={filteredLogs} activeTag={activeTag} />
      )}

      <div className="admin-export">
        <h3>Export My Data</h3>

        <button
          onClick={() => handleExportLogs("json")}
          disabled={loadingExportLogs}
        >
          Export Logs (JSON)
          {loadingExportLogs && <span className="spinner" />}
        </button>

        <button
          onClick={() => handleExportLogs("txt")}
          disabled={loadingExportLogs}
        >
          Export Logs (TXT)
          {loadingExportLogs && <span className="spinner" />}
        </button>

        <br />

        <button
          onClick={() => handleExportSummaries("json")}
          disabled={loadingExportSummaries}
        >
          Export Summaries (JSON)
          {loadingExportSummaries && <span className="spinner" />}
        </button>

        <button
          onClick={() => handleExportSummaries("txt")}
          disabled={loadingExportSummaries}
        >
          Export Summaries (TXT)
          {loadingExportSummaries && <span className="spinner" />}
        </button>
      </div>
    </div>
  );
}
