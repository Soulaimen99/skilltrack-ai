/**
 * Checks if a given date string is within a specified date range.
 *
 * @param {string} dateStr - The date string to check.
 * @param {string | null} from - The start date of the range in YYYY-MM-DD format, or null for no start date.
 * @param {string | null} to - The end date of the range in YYYY-MM-DD format, or null for no end date.
 * @return {boolean} True if the date is within the range, or false otherwise.
 */
export function isDateInRange( dateStr, from, to ) {
	const date = new Date( dateStr );
	const fromDate = from ? new Date( from + "T00:00:00" ) : null;
	const toDate = to ? new Date( to + "T23:59:59" ) : null;
	return (!fromDate || date >= fromDate) && (!toDate || date <= toDate);
}

function formatDate( date ) {
	return date.toISOString().split( "T" )[0]; // yyyy-mm-dd
}

/**
 * Retrieves the date range for the current day.
 *
 * @return {Object} An object containing the start and end of today's range in the format { from: string, to: string }.
 */
export function getTodayRange() {
	const today = new Date();
	const formatted = formatDate( today );
	return { from: formatted, to: formatted };
}

/**
 * Calculates the date range for the current week, starting from Monday and ending on Sunday.
 *
 * @return {{from: string, to: string}} An object containing the start date (`from`) and end date (`to`) of the current week in formatted string representation.
 */
export function getThisWeekRange() {
	const today = new Date();
	const day = today.getDay(); // 0 (Sun) - 6 (Sat)
	const diffToMonday = (day + 6) % 7;
	const monday = new Date( today );
	monday.setDate( today.getDate() - diffToMonday );
	const sunday = new Date( monday );
	sunday.setDate( monday.getDate() + 6 );
	return { from: formatDate( monday ), to: formatDate( sunday ) };
}

/**
 * Returns the date range for the current month, including the first and last days of the month.
 *
 * @return {Object} An object with two properties:
 * - `from`: A string representing the first day of the current month.
 * - `to`: A string representing the last day of the current month.
 */
export function getThisMonthRange() {
	const now = new Date();
	const first = new Date( now.getFullYear(), now.getMonth(), 1 );
	const last = new Date( now.getFullYear(), now.getMonth() + 1, 0 );
	return { from: formatDate( first ), to: formatDate( last ) };
}
