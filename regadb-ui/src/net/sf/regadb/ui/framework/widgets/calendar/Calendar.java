package net.sf.regadb.ui.framework.widgets.calendar;
import java.util.Date;

import net.sf.regadb.ui.framework.widgets.messagebox.MessageBox;
import net.sf.witty.wt.i8n.WMessage;
import net.sf.witty.wt.widgets.SignalListener;
import net.sf.witty.wt.widgets.WComboBox;
import net.sf.witty.wt.widgets.WContainerWidget;
import net.sf.witty.wt.widgets.WPushButton;
import net.sf.witty.wt.widgets.WTable;
import net.sf.witty.wt.widgets.WText;
import net.sf.witty.wt.widgets.event.WEmptyEvent;
import net.sf.witty.wt.widgets.event.WMouseEvent;

public class Calendar extends WContainerWidget
{
	private WContainerWidget comboControls_;
	private WComboBox monthCombo_;
	private WComboBox yearCombo_;
	private WPushButton pushToday_;
	
	private boolean showPrevious_;
	private WTable calendarTable_;

	private ICalendarType calendarType_;
	
	public Calendar(WContainerWidget parent, final int startYear, int endYear, boolean showPrevious,  Date startDate)
	{
		super(parent);
		comboControls_ = new WContainerWidget(this);
		calendarType_ = new GregorianCalendarType();
		
		createComboControls(startYear, endYear);

		calendarTable_ = new WTable(this);
		
		createCalendarTable();
		showPrevious_=showPrevious;
		fillCalendarTable(startDate);
		
		pushToday_=new WPushButton(tr("calendar.today"));
		//this.addWidget(pushToday_);
		pushToday_.clicked.addListener(new SignalListener<WMouseEvent>()
		{
			public void notify(WMouseEvent a) 
				{
					Date date=new Date(System.currentTimeMillis());
					fillCalendarTable(date);
					monthCombo_.setCurrentIndex(calendarType_.getMonth(date));
					yearCombo_.setCurrentIndex(calendarType_.getYearIndex(date,startYear));
				}
			});
		}
	

	public Calendar(WContainerWidget parent, int startYear, int endYear)
	{
		this(parent, startYear, endYear, true, new Date(System.currentTimeMillis()));
	}

	public Calendar(WContainerWidget parent, int startYear, int endYear, boolean showPrevious) 
	{
		this(parent, startYear, endYear, showPrevious, new Date(System.currentTimeMillis()));
	}

	private void createComboControls(int startYear, int endYear)
	{
		monthCombo_ = new WComboBox(comboControls_);
		monthCombo_.setStyleClass("header-combo");
		for(WMessage month : calendarType_.getMonths())
		{
			monthCombo_.addItem(month);
		}
		monthCombo_.setCurrentIndex(calendarType_.getMonth(new Date(System.currentTimeMillis())));
		monthCombo_.changed.addListener(new SignalListener<WEmptyEvent>()
		{
			public void notify(WEmptyEvent a) 
			{
				refreshCalendar();
			}
		});
		yearCombo_ = new WComboBox(comboControls_);
		yearCombo_ .setStyleClass("header-combo");
		for (int i = startYear; i <= endYear; i++)
		{
			yearCombo_.addItem(lt(i + ""));
		}
		yearCombo_.setCurrentIndex(calendarType_.getYearIndex(new Date(System.currentTimeMillis()),startYear));
		yearCombo_.changed.addListener (new SignalListener<WEmptyEvent>()
		{
			public void notify(WEmptyEvent a) 
			{
				refreshCalendar();
			}
		});
	}

	protected void refreshCalendar() 
	{
		int selectedMonth, selectedYear;
		selectedMonth=monthCombo_.currentIndex();
		selectedYear=Integer.parseInt(yearCombo_.currentText().keyOrValue());
		Date selectedDate=calendarType_.getDate(selectedMonth,selectedYear);
		fillCalendarTable(selectedDate); 
	}

	private void createCalendarTable()
	{
		int row = 0;
		int col = 0;

		for(WMessage dayMsg : calendarType_.getWeekDays())
		{
			createDayNameCell(row, col, dayMsg);
			col++;
		}

		row++;
		col = 0;

		int amountOfWeekDays = calendarType_.getWeekDays().length;
		int maxOfWeekLines = calendarType_.getMaxAmountOfDaysInMonth()/amountOfWeekDays + 3;
		
		for (; row < maxOfWeekLines; row++)
		{
			for (col = 0; col < amountOfWeekDays; col++)
			{
				createDayCell(row, col);
			}
		}
	}

	private void createDayNameCell(int row, int col, WMessage dayName)
	{
		WText wtext =new WText(dayName);
		//wtext.setStyleClass("dayheaders");
		calendarTable_.elementAt(row, col).addWidget(wtext);
		calendarTable_.elementAt(row, col).setStyleClass("dayheaders");
	}

	private void createDayCell(int row, int col)
	{
		final WText dateText = new WText();
		calendarTable_.elementAt(row, col).addWidget(dateText);
		calendarTable_.elementAt(row, col).setStyleClass("otherdays-of-month");
		dateText.clicked.addListener(new SignalListener<WMouseEvent>()
		{
			public void notify(WMouseEvent a) 
			{
				int clickedday=Integer.parseInt(dateText.text().value());
				int selectedMonth=monthCombo_.currentIndex();
				int selectedYear=Integer.parseInt(yearCombo_.currentText().keyOrValue());
				Date selectedDate=calendarType_.getDate(clickedday,selectedMonth,selectedYear);
				MessageBox.showWarningMessage(lt("You have selected a date " + selectedDate));
			}
		});
	}

	private void fillCalendarTable(Date date)
	{
		int amountOfWeekDays = calendarType_.getWeekDays().length;
		int maxOfWeekLines = calendarType_.getMaxAmountOfDaysInMonth()/amountOfWeekDays + 2;
		int firstDayOfMonthPosition = calendarType_.getFirstDayOfMonthIndex(date);
		int amountOfDaysInThisMonth =calendarType_.getAmountOfDaysInMonth(date);
		int amountOfDaysInPreviousMonth = calendarType_.getAmountOfDaysInPreviousMonth(date);
			
		int row=1;
		
        for(int col = 0; col < firstDayOfMonthPosition; col++)
			{
				WText calendarField = (WText) calendarTable_.elementAt(row, col).children().get(0);
				if (showPrevious_)
                {
                    calendarField.setText(lt(amountOfDaysInPreviousMonth-firstDayOfMonthPosition+col+1+""));
                }
                else
                {
                    calendarField.setText(lt(""));
                }
			}	
		
		int startDate=1;
		
		int col = firstDayOfMonthPosition;
		boolean firstRoundUp = true;
		boolean nextMonth=false;
		for (; row < maxOfWeekLines+1; row++)
		{
			if(firstRoundUp)
			{
				firstRoundUp = false;
			}
			else
			{
				col = 0;
			}
			for(; col<amountOfWeekDays; col++,startDate++)
			{
				if(startDate>amountOfDaysInThisMonth)
				{
					startDate = 1;
					nextMonth = true;
				}
				WText calendarField = (WText) calendarTable_.elementAt(row, col).children().get(0);
				if(nextMonth && !showPrevious_)
				{
					calendarField.setText(lt(""));
				}
				else
				{
					calendarField.setText(lt(startDate + ""));
                }
			}
		}
	}

	public ICalendarType getCalendarType() 
	{
		return calendarType_;
	}

	public void setCalendarType(ICalendarType calendarType) 
	{
		calendarType_ = calendarType;
	}
}