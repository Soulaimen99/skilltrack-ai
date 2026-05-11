import {useCallback, useEffect, useState} from "react";
import LogList from "../components/LogList";
import TagFilter from "../components/TagFilter";
import DateFilter from "../components/DateFilter";
import useFetch from "../hooks/useFetch";
import ErrorMessage from "../components/ErrorMessage";
import LoadingSpinner from "../components/LoadingSpinner";

/**
 * Component that provides an interface to manage, view, and export learning logs and summaries.
 * Allows users to add new logs, filter logs based on tags or date ranges, and export logs or summaries in various formats.
 *
 * @return {JSX.Element} The rendered LogsPage component containing log management tools and export functionality.
 */
export default function LogsPage() {
	const { get, post, downloadFile, error } = useFetch();
	const [logs, setLogs] = useState( [] );
	const [content, setContent] = useState( "" );
	const [tags, setTags] = useState( "" );
	const [loadingLogs, setLoadingLogs] = useState( false );
	const [loadingExportLogs, setLoadingExportLogs] = useState( false );
	const [loadingExportSummaries, setLoadingExportSummaries] = useState( false );
	const [activeTag, setActiveTag] = useState( null );
	const [dateRange, setDateRange] = useState( { from: "", to: "" } );
	const [presetRange, setPresetRange] = useState( "all" );

	const fetchLogs = useCallback( async () => {
		setLoadingLogs( true );
		try {
			const data = await get( "/api/logs?page=0&size=100" );
			setLogs( data.content || [] );
		}
		catch ( err ) {
			console.error( "Failed to fetch logs", err );
		}
		finally {
			setLoadingLogs( false );
		}
	}, [get] );

	useEffect( () => {
		fetchLogs();
	}, [fetchLogs] );

	const handleAddLog = async ( e ) => {
		e.preventDefault();
		try {
			const newLog = await post( "/api/logs", { content, tags } );
			setLogs( ( prev ) => [...prev, newLog] );
			setContent( "" );
			setTags( "" );
		}
		catch ( err ) {
			console.error( "Failed to add log", err );
		}
	};

	const handleExportLogs = async ( format = "json" ) => {
		setLoadingExportLogs( true );
		try {
			await downloadFile( `/api/logs/export?format=${format}`, `learning_logs.${format}` );
		}
		catch ( err ) {
			console.error( "Failed to export logs:", err );
		}
		finally {
			setLoadingExportLogs( false );
		}
	};

	const handleExportSummaries = async ( format = "json" ) => {
		setLoadingExportSummaries( true );
		try {
			await downloadFile( `/api/summaries/export?format=${format}`, `summaries.${format}` );
		}
		catch ( err ) {
			console.error( "Failed to export summaries:", err );
		}
		finally {
			setLoadingExportSummaries( false );
		}
	};

	const uniqueTags = Array.from(
		new Set(
			logs.flatMap( ( log ) =>
				(log.tags || "")
					.split( "," )
					.map( ( tag ) => tag.trim() )
					.filter( Boolean )
			)
		)
	);

	const filteredLogs = logs.filter( ( log ) => {
		const matchesTag =
			!activeTag ||
			(log.tags || "")
				.split( "," )
				.map( ( t ) => t.trim() )
				.includes( activeTag );
		const matchesDate =
			(!dateRange.from || new Date( log.date ) >= new Date( dateRange.from )) &&
			(!dateRange.to || new Date( log.date ) <= new Date( dateRange.to ));
		return matchesTag && matchesDate;
	} );

	return (
		<div className="container">
			<h2>My Learning Logs</h2>

			<ErrorMessage message={error}/>

			<form onSubmit={handleAddLog}>
        <textarea
	        value={content}
	        onChange={( e ) => setContent( e.target.value )}
	        placeholder="What did you learn?"
	        rows="4"
	        required
        />
				<input
					type="text"
					value={tags}
					onChange={( e ) => setTags( e.target.value )}
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
				<LoadingSpinner label="Loading logs..."/>
			) : (
				<LogList logs={filteredLogs} activeTag={activeTag}/>
			)}

			<div className="admin-export">
				<h3>Export My Data</h3>

				<button
					onClick={() => handleExportLogs( "json" )}
					disabled={loadingExportLogs}
				>
					Export Logs (JSON)
					{loadingExportLogs && <span className="spinner"/>}
				</button>

				<button
					onClick={() => handleExportLogs( "txt" )}
					disabled={loadingExportLogs}
				>
					Export Logs (TXT)
					{loadingExportLogs && <span className="spinner"/>}
				</button>

				<br/>

				<button
					onClick={() => handleExportSummaries( "json" )}
					disabled={loadingExportSummaries}
				>
					Export Summaries (JSON)
					{loadingExportSummaries && <span className="spinner"/>}
				</button>

				<button
					onClick={() => handleExportSummaries( "txt" )}
					disabled={loadingExportSummaries}
				>
					Export Summaries (TXT)
					{loadingExportSummaries && <span className="spinner"/>}
				</button>
			</div>
		</div>
	);
}
