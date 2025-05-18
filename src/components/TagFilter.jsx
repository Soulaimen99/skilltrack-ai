import React from "react";

/**
 * A TagFilter component that allows users to filter items by tags. Users can select a tag to filter or reset the selection to view all items.
 *
 * @param {Object} props - The properties object.
 * @param {string[]} props.allTags - The array of available tags to filter by.
 * @param {?string} props.activeTag - The currently active tag. Null if no tag is active.
 * @param {Function} props.setActiveTag - The function to update the active tag. Accepts either a tag string or null to reset.
 *
 * @return {JSX.Element} The rendered TagFilter component.
 */
export default function TagFilter( { allTags, activeTag, setActiveTag } ) {
	return (
		<div className="tag-filter">
			<button
				onClick={() => setActiveTag( null )}
				className={!activeTag ? "active" : ""}
			>
				All
			</button>

			{allTags.map( ( tag ) => (
				<button
					key={tag}
					onClick={() => setActiveTag( tag )}
					className={activeTag === tag ? "active" : ""}
				>
					{tag}
				</button>
			) )}
		</div>
	);
}
