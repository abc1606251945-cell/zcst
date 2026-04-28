export const DUTY_PERIODS = [
  "08:30-10:00",
  "10:00-12:30",
  "12:30-15:10",
  "15:10-18:00",
  "18:00-20:00",
  "20:00-22:00",
]

export const DUTY_PERIOD_OPTIONS = DUTY_PERIODS.map((item) => ({
  label: item,
  value: item,
}))

const legacyDutyPeriodMap = {
  "1-2节": "08:30-10:00",
  "3-4节": "10:00-12:30",
  "5-6节": "12:30-15:10",
  "7-8节": "15:10-18:00",
  "9-10节": "18:00-20:00",
  "11-12节": "20:00-22:00",
}

export function normalizeDutyPeriod(period) {
  return legacyDutyPeriodMap[period] || period
}
