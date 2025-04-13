import React from 'react';

export default function TagFilter({ allTags, activeTag, setActiveTag }) {
    return (
        <div className="tag-filter">
            <button onClick={() => setActiveTag(null)} className={!activeTag ? 'active' : ''}>All</button>
            {allTags.map(tag => (
                <button
                    key={tag}
                    onClick={() => setActiveTag(tag)}
                    className={activeTag === tag ? 'active' : ''}
                >
                    {tag}
                </button>
            ))}
        </div>
    );
}