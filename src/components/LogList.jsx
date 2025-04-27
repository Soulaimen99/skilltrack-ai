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
    return <p>No learning logs available.</p>;
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
              <div className="input-left">
                <textarea
                  value={editContent}
                  onChange={(e) => setEditContent(e.target.value)}
                  rows={3}
                  placeholder="Edit your learning..."
                />
                <input
                  type="text"
                  value={editTags}
                  onChange={(e) => setEditTags(e.target.value)}
                  placeholder="Tags (comma separated)"
                />
                <div className="input-buttons">
                  <button onClick={saveEdit} title="Save">
                    💾
                  </button>
                  <button onClick={cancelEdit} title="Cancel">
                    ❌
                  </button>
                </div>
              </div>
            ) : (
              <>
                <div className="input-left">
                  {log.goalTitle && (
                    <div className="input-first">
                      <span className="label">Goal:</span> {log.goalTitle}
                    </div>
                  )}
                  <div className="input-second">
                    <span className="label">Content:</span> {log.content}
                  </div>
                  <div className="input-third">
                    <span className="label">Tags:</span> {log.tags || "—"}
                  </div>
                  <div className="input-footer">
                    {new Date(log.date).toLocaleDateString()}{" "}
                    {new Date(log.date).toLocaleTimeString([], {
                      hour: "2-digit",
                      minute: "2-digit",
                    })}
                  </div>
                </div>

                {!readOnly && (
                  <div className="input-buttons">
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
