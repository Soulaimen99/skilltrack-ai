import { useEffect, useState, useMemo, useCallback } from "react";
import TagFilter from "../components/TagFilter";
import DateFilter from "../components/DateFilter";
import LogList from "../components/LogList";
import SummaryList from "../components/SummaryList";
import ErrorMessage from "../components/ErrorMessage";
import LoadingSpinner from "../components/LoadingSpinner";
import useFetch from "../hooks/useFetch";
import { isDateInRange } from "../utils/dateUtils";

export default function AdminPanel() {
  const { get, loading: fetchLoading, error: fetchError, downloadData } = useFetch();

  const [allUsers, setAllUsers] = useState([]);
  const [selectedUserId, setSelectedUserId] = useState("");
  const [allLogs, setAllLogs] = useState([]);
  const [summaries, setSummaries] = useState([]);
  const [activeTag, setActiveTag] = useState(null);
  const [presetRange, setPresetRange] = useState("all");
  const [dateRange, setDateRange] = useState("");
  const [loadingExportLogs, setLoadingExportLogs] = useState(false);
  const [loadingExportSummaries, setLoadingExportSummaries] = useState(false);
  const [error, setError] = useState(null);

  // Memoize filtered logs for better performance
  const logs = useMemo(() => {
    return allLogs.filter((log) => {
      const matchesTag =
        !activeTag ||
        (log.tags || "")
          .split(",")
          .map((t) => t.trim())
          .includes(activeTag);
      const inDateRange = isDateInRange(log.date, dateRange.from, dateRange.to);
      return matchesTag && inDateRange;
    });
  }, [activeTag, dateRange, allLogs]);

  // Memoize all tags for better performance
  const allTags = useMemo(() => {
    return Array.from(
      new Set(
        allLogs.flatMap((log) => log.tags?.split(",").map((t) => t.trim()) || [])
      )
    ).filter(Boolean);
  }, [allLogs]);

  // Fetch all users
  useEffect(() => {
    const fetchUsers = async () => {
      try {
        const data = await get("/api/admin/users");
        setAllUsers(data);
      } catch {
        setError("Failed to load users. Please try again.");
      }
    };

    fetchUsers();
  }, [get]);

  // Fetch logs and summaries when user or date range changes
  useEffect(() => {
    if (!selectedUserId) return;

    const fetchLogsAndSummaries = async () => {
      try {
        setError(null);
        const params = new URLSearchParams();
        if (dateRange.from) params.append("from", dateRange.from);
        if (dateRange.to) params.append("to", dateRange.to);
        params.append("page", "0");
        params.append("size", "100");

        const logsUrl = `/api/admin/users/${selectedUserId}/logs?${params.toString()}`;
        const summariesUrl = `/api/admin/users/${selectedUserId}/summaries?${params.toString()}`;

        const [logsData, summariesData] = await Promise.all([
          get(logsUrl),
          get(summariesUrl)
        ]);

        setAllLogs(logsData.content || []);
        setSummaries(summariesData.content || []);
      } catch {
        setError("Failed to load user data. Please try again.");
        setAllLogs([]);
        setSummaries([]);
      }
    };

    fetchLogsAndSummaries();
  }, [selectedUserId, dateRange, get]);

  // Handle exporting logs
  const handleExportUserLogs = useCallback(async (format = "json") => {
    if (!selectedUserId) return;

    setLoadingExportLogs(true);
    try {
      const params = new URLSearchParams();
      if (dateRange.from) params.append("from", dateRange.from);
      if (dateRange.to) params.append("to", dateRange.to);

      const url = `/api/admin/users/${selectedUserId}/logs?${params.toString()}`;
      const data = await get(url);

      downloadData(
        data.content, 
        `user_${selectedUserId}_logs.${format}`, 
        format
      );
    } catch {
      setError("Failed to export logs. Please try again.");
    } finally {
      setLoadingExportLogs(false);
    }
  }, [selectedUserId, dateRange, get, downloadData]);

  // Handle exporting summaries
  const handleExportUserSummaries = useCallback(async (format = "json") => {
    if (!selectedUserId) return;

    setLoadingExportSummaries(true);
    try {
      const params = new URLSearchParams();
      if (dateRange.from) params.append("from", dateRange.from);
      if (dateRange.to) params.append("to", dateRange.to);

      const url = `/api/admin/users/${selectedUserId}/summaries?${params.toString()}`;
      const data = await get(url);

      downloadData(
        data.content, 
        `user_${selectedUserId}_summaries.${format}`, 
        format
      );
    } catch {
      setError("Failed to export summaries. Please try again.");
    } finally {
      setLoadingExportSummaries(false);
    }
  }, [selectedUserId, dateRange, get, downloadData]);

  // Clear error when component unmounts or when user changes
  useEffect(() => {
    return () => setError(null);
  }, [selectedUserId]);

  return (
    <div className="container">
      <h2>Admin Panel</h2>

      {/* Display any errors */}
      <ErrorMessage 
        message={error || fetchError} 
        onDismiss={() => setError(null)} 
      />

      {/* User selection dropdown */}
      <label>
        <strong>Select user:</strong>
        <select
          value={selectedUserId}
          onChange={(e) => setSelectedUserId(e.target.value)}
          aria-label="Select user"
        >
          <option value="">-- Choose a user --</option>
          {allUsers.map((u) => (
            <option key={u.id} value={u.id}>
              {u.username} ({u.email})
            </option>
          ))}
        </select>
      </label>

      {/* Loading indicator for initial users fetch */}
      {fetchLoading && !selectedUserId && (
        <LoadingSpinner label="Loading users..." />
      )}

      {selectedUserId && (
        <>
          <div className="filters">
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
          </div>

          <h3>User Logs</h3>
          {fetchLoading ? (
            <LoadingSpinner label="Loading logs..." />
          ) : (
            <LogList logs={logs} readOnly={true} />
          )}

          <h3>User Summaries</h3>
          {fetchLoading ? (
            <LoadingSpinner label="Loading summaries..." />
          ) : (
            <SummaryList summaries={summaries} />
          )}

          <div className="admin-export">
            <h3>Export User Data</h3>
            <button
              onClick={() => handleExportUserLogs("json")}
              disabled={loadingExportLogs || fetchLoading}
              aria-label="Export logs as JSON"
            >
              Export Logs (JSON)
              {loadingExportLogs && <span className="spinner" aria-hidden="true" />}
            </button>

            <button
              onClick={() => handleExportUserSummaries("json")}
              disabled={loadingExportSummaries || fetchLoading}
              aria-label="Export summaries as JSON"
            >
              Export Summaries (JSON)
              {loadingExportSummaries && <span className="spinner" aria-hidden="true" />}
            </button>
          </div>
        </>
      )}
    </div>
  );
}
