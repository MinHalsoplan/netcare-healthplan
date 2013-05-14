/*
 * jQuery Mobile Framework : plugin to provide a date and time picker.
 * Copyright (c) JTSage
 * CC 3.0 Attribution.  May be relicensed without permission/notifcation.
 * https://github.com/jtsage/jquery-mobile-datebox
 *
 * Translation by: Unknown
 *
 */

jQuery.extend(jQuery.mobile.datebox.prototype.options.lang, {
	'sv-SE': {
		setDateButtonLabel: "Välj datum",
		setTimeButtonLabel: "Välj tid",
		setDurationButtonLabel: "Välj varaktighet",
		calTodayButtonLabel: "Gå till idag",
		titleDateDialogLabel: "Välj datum",
		titleTimeDialogLabel: "Välj tid",
		daysOfWeek: ["Söndag", "Måndag", "Tisdag", "Onsdag", "Torsdag", "Fredag", "Lördag"],
		daysOfWeekShort: ["Sö", "Må", "Ti", "On", "To", "Fr", "Lö"],
		monthsOfYear: ["Januari", "Februari", "Mars", "April", "Maj", "Juni", "July", "Augusti", "September", "Oktober", "November", "December"],
		monthsOfYearShort: ["Jan", "Feb", "Mar", "Apr", "Maj", "Jun", "Jul", "Aug", "Sep", "Okt", "Nov", "Dec"],
		durationLabel: ["Dagar", "Timmar", "Minuter", "Sekunder"],
		durationDays: ["Dag", "Dagar"],
		tooltip: "Öppna datumväljare",
		nextMonth: "Nästa månad",
		prevMonth: "Föregående månad",
		timeFormat: 24,
		headerFormat: '%A, %B %-d, %Y',
		dateFieldOrder: ['y','m','d'],
		timeFieldOrder: ['h', 'i', 'a'],
		slideFieldOrder: ['y', 'm', 'd'],
		dateFormat: "%Y-%m-%d",
		useArabicIndic: false,
		isRTL: false,
		calStartDay: 0,
		clearButton: "Klart",
		durationOrder: ['d', 'h', 'i', 's'],
		meridiem: ["AM", "PM"],
		timeOutput: "%k:%M",
		durationFormat: "%Dd %DA, %Dl:%DM:%DS",
		calDateListLabel: "Andra datum"
	}
});
jQuery.extend(jQuery.mobile.datebox.prototype.options, {
	useLang: 'sv-SE'
});

