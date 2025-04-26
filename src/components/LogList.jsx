import React, { useState } from "react";

export default function LogList({
  logs,
  activeTag,
  handleDelete,
  handleUpdate,
  readOnly = false,
}) {
  const [editId, setEditId] = useState(null);
  const [editContent, setEditContent] = useState("");
  const [editTags, setEditTags] = useState("");

  const startEdit = (log) => {
    setEditId(log.id);
    setEditContent(log.content);
    setEditTags(log.tags || "");
  };

  const cancelEdit = () => {
    setEditId(null);
    setEditContent("");
    setEditTags("");
  };

  const saveEdit = () => {
    handleUpdate(editId, editContent, editTags);
    cancelEdit();
  };

  if (logs.length === 0) {
    return <p>No Learning logs available.</p>;
  }

  return (
    <ul>
      {logs
        .filter(
          (log) =>
            !activeTag ||
            (log.tags || "")
              .split(",")
              .map((t) => t.trim())
              .includes(activeTag)
        )
        .map((log) => (
          <li key={log.id}>
            {editId === log.id ? (
              <div className="log-edit" style={{ width: "100%" }}>
                <textarea
                  value={editContent}
                  onChange={(e) => setEditContent(e.target.value)}
                  rows={3}
                />
                <input
                  type="text"
                  value={editTags}
                  onChange={(e) => setEditTags(e.target.value)}
                  placeholder="Tags (comma separated)"
                />
                <div>
                  <button onClick={saveEdit}>💾 Save</button>
                  <button onClick={cancelEdit}>❌ Cancel</button>
                </div>
              </div>
            ) : (
              <>
                <div className="log-left">
                  <div className="log-main">
                    <span className="label">Content:</span> {log.content}
                  </div>
                  <div className="log-tags">
                    <span className="label">Tags:</span> {log.tags || "—"}
                  </div>
                  <div className="log-footer">
                    {new Date(log.date).toLocaleString()}
                  </div>
                </div>
                {!readOnly && (
                  <div className="log-buttons">
                    <button onClick={() => startEdit(log)} title="Edit log">
                      ✏️
                    </button>
                    <button
                      onClick={() => handleDelete(log.id)}
                      title="Delete log"
                    >
                      🗑️
                    </button>
                  </div>
                )}
              </>
            )}
          </li>
        ))}
    </ul>
  );
}
