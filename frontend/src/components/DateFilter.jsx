import React from "react";
import {getThisMonthRange, getThisWeekRange, getTodayRange,} from "../utils/dateUtils";

/**
 * A component that provides controls for filtering data based on a date range or preset date ranges.
 *
 * @param {Object} props The properties object.
 * @param {Object} props.dateRange The current date range filter, containing `from` and `to` properties.
 * @param {Function} props.setDateRange Function to update the `dateRange` state.
 * @param {string} props.presetRange The current preset range selection, such as "all", "today", "week", or "month".
 * @param {Function} props.setPresetRange Function to update the `presetRange` state.
 * @return {JSX.Element} A JSX element rendering the date filter UI.
 */
export default function DateFilter( {
	                                    dateRange,
	                                    setDateRange,
	                                    presetRange,
	                                    setPresetRange,
                                    } ) {
	const setAll = () => {
		setDateRange( "" );
		setPresetRange( "all" );
	};
	const setToday = () => {
		setDateRange( getTodayRange() );
		setPresetRange( "today" );
	};
	const setThisWeek = () => {
		setDateRange( getThisWeekRange() );
		setPresetRange( "week" );
	};
	const setThisMonth = () => {
		setDateRange( getThisMonthRange() );
		setPresetRange( "month" );
	};

	return (
		<div className="date-filter">
			<div className="preset-buttons">
				<button
					onClick={setAll}
					className={presetRange === "all" ? "active" : ""}
				>
					All
				</button>
				<button
					onClick={setToday}
					className={presetRange === "today" ? "active" : ""}
				>
					Today
				</button>
				<button
					onClick={setThisWeek}
					className={presetRange === "week" ? "active" : ""}
				>
					This Week
				</button>
				<button
					onClick={setThisMonth}
					className={presetRange === "month" ? "active" : ""}
				>
					This Month
				</button>
			</div>

			<label>
				<strong>From:</strong>
				<input
					type="date"
					value={dateRange.from || ""}
					onChange={( e ) =>
						setDateRange( ( prev ) => ({ ...prev, from: e.target.value }) )
					}
					max={dateRange.to || undefined}
				/>
			</label>
			<label>
				<strong>To:</strong>
				<input
					type="date"
					value={dateRange.to || ""}
					onChange={( e ) =>
						setDateRange( ( prev ) => ({ ...prev, to: e.target.value }) )
					}
					min={dateRange.from || undefined}
				/>
			</label>
		</div>
	);
}
