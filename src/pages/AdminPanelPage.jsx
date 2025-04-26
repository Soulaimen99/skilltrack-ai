import { useEffect, useState } from "react";
import { useKeycloak } from "@react-keycloak/web";
import TagFilter from "../components/TagFilter";
import DateFilter from "../components/DateFilter";
import LogList from "../components/LogList";
import SummaryList from "../components/SummaryList";
import { isDateInRange } from "../utils/dateUtils";

export default function AdminPanel() {
  const { keycloak } = useKeycloak();
  const [allUsers, setAllUsers] = useState([]);
  const [selectedUserId, setSelectedUserId] = useState("");
  const [logs, setLogs] = useState([]);
  const [allLogs, setAllLogs] = useState([]);
  const [summaries, setSummaries] = useState([]);
  const [loading, setLoading] = useState(false);
  const [activeTag, setActiveTag] = useState(null);
  const [presetRange, setPresetRange] = useState("all");
  const [dateRange, setDateRange] = useState("");
  const [loadingExportLogs, setLoadingExportLogs] = useState(false);
  const [loadingExportSummaries, setLoadingExportSummaries] = useState(false);

  const allTags = Array.from(
    new Set(
      allLogs.flatMap((log) => log.tags?.split(",").map((t) => t.trim()) || [])
    )
  ).filter(Boolean);

  useEffect(() => {
    fetch("/admin/users", {
      headers: { Authorization: `Bearer ${keycloak.token}` },
    })
      .then((res) => res.json())
      .then(setAllUsers)
      .catch(console.error);
  }, [keycloak]);

  useEffect(() => {
    if (!selectedUserId) return;
    setLoading(true);

    const params = new URLSearchParams();
    if (dateRange.from) params.append("from", dateRange.from);
    if (dateRange.to) params.append("to", dateRange.to);
    params.append("page", "0");
    params.append("size", "100");

    const fetchLogsAndSummary = async () => {
      try {
        const logsRes = await fetch(
          `/admin/users/${selectedUserId}/logs?${params.toString()}`,
          {
            headers: { Authorization: `Bearer ${keycloak.token}` },
          }
        );
        const logsData = await logsRes.json();
        const logs = logsData.content || [];
        setAllLogs(logs);

        const summariesRes = await fetch(
          `/admin/users/${selectedUserId}/summaries?${params.toString()}`,
          {
            headers: { Authorization: `Bearer ${keycloak.token}` },
          }
        );
        const summariesData = await summariesRes.json();
        setSummaries(summariesData.content || "");
      } catch (error) {
        console.error("Error fetching logs or summary:", error);
      } finally {
        setLoading(false);
      }
    };

    fetchLogsAndSummary();
  }, [selectedUserId, dateRange, keycloak]);

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

  const handleExportUserLogs = async (format = "json") => {
    if (!selectedUserId) return;
    setLoadingExportLogs(true);
    try {
      const params = new URLSearchParams();
      if (dateRange.from) params.append("from", dateRange.from);
      if (dateRange.to) params.append("to", dateRange.to);

      const res = await fetch(
        `/admin/users/${selectedUserId}/logs?${params.toString()}`,
        {
          headers: { Authorization: `Bearer ${keycloak.token}` },
        }
      );

      if (!res.ok) throw new Error(await res.text());
      const result = await res.json();
      const blob = new Blob([JSON.stringify(result.content, null, 2)], {
        type: "application/json",
      });
      const url = URL.createObjectURL(blob);

      const a = document.createElement("a");
      a.href = url;
      a.download = `user_${selectedUserId}_logs.${format}`;
      a.click();
      URL.revokeObjectURL(url);
    } catch (err) {
      console.error("Failed to export user logs:", err);
    } finally {
      setLoadingExportLogs(false);
    }
  };

  const handleExportUserSummaries = async (format = "json") => {
    if (!selectedUserId) return;
    setLoadingExportSummaries(true);
    try {
      const params = new URLSearchParams();
      if (dateRange.from) params.append("from", dateRange.from);
      if (dateRange.to) params.append("to", dateRange.to);

      const res = await fetch(
        `/admin/users/${selectedUserId}/summaries?${params.toString()}`,
        {
          headers: { Authorization: `Bearer ${keycloak.token}` },
        }
      );

      if (!res.ok) throw new Error(await res.text());
      const result = await res.json();
      const blob = new Blob([JSON.stringify(result.content, null, 2)], {
        type: "application/json",
      });
      const url = URL.createObjectURL(blob);

      const a = document.createElement("a");
      a.href = url;
      a.download = `user_${selectedUserId}_summaries.${format}`;
      a.click();
      URL.revokeObjectURL(url);
    } catch (err) {
      console.error("Failed to export user summaries:", err);
    } finally {
      setLoadingExportSummaries(false);
    }
  };

  return (
    <div className="admin-panel">
      <h2>Admin Panel</h2>
      <label>
        Select user:
        <select
          value={selectedUserId}
          onChange={(e) => setSelectedUserId(e.target.value)}
        >
          <option value="">-- Choose a user --</option>
          {allUsers.map((u) => (
            <option key={u.id} value={u.id}>
              {u.username} ({u.email})
            </option>
          ))}
        </select>
      </label>

      {selectedUserId && (
        <>
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

          <h3>User Logs</h3>
          {loading ? (
            <p>Loading logs...</p>
          ) : (
            <LogList logs={logs} activeTag={activeTag} readOnly={true} />
          )}

          <h3>User Summaries</h3>
          {loading ? (
            <p>Loading summaries...</p>
          ) : (
            <SummaryList summaries={summaries} />
          )}

          <div className="admin-export">
            <h3>Export User Data</h3>
            <button
              onClick={() => handleExportUserLogs("json")}
              disabled={loadingExportLogs}
            >
              Export Logs (JSON)
              {loadingExportLogs && <span className="spinner" />}
            </button>

            <button
              onClick={() => handleExportUserSummaries("json")}
              disabled={loadingExportSummaries}
            >
              Export Summaries (JSON)
              {loadingExportSummaries && <span className="spinner" />}
            </button>
          </div>
        </>
      )}
    </div>
  );
}
