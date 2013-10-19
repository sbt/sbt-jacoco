package de.johoop.jacoco4sbt;

import org.jacoco.core.analysis.ICoverageNode;
import org.jacoco.report.html.HTMLFormatter;
import org.jacoco.report.internal.html.resources.Styles;
import org.jacoco.report.internal.html.table.BarColumn;
import org.jacoco.report.internal.html.table.CounterColumn;
import org.jacoco.report.internal.html.table.LabelColumn;
import org.jacoco.report.internal.html.table.Table;

/**
 * Omits displaying instruction and branch coverage in the coverage tables, as Scala generates null checks which make these too noisy
 *
 * TODO: Find a way to remove them from the annotated source code reports, too.
 */
public class ScalaHtmlFormatter extends HTMLFormatter {
	private Table table;

	public ScalaHtmlFormatter() {
        setLanguageNames(new ScalaLanguageNames());
	}

	public Table getTable() {
		if (table == null) {
			table = createTable();
		}
		return table;
	}

	private Table createTable() {
		final Table t = new Table();
		t.add("Element", null, new LabelColumn(), false);

        // Just show line coverage in Scala projects.
        //		t.add("Missed Instructions", Styles.BAR, new BarColumn(ICoverageNode.CounterEntity.INSTRUCTION,
        //				locale), true);
        //		t.add("Cov.", Styles.CTR2,
        //				new PercentageColumn(ICoverageNode.CounterEntity.INSTRUCTION, locale), false);
        //		t.add("Missed Branches", Styles.BAR, new BarColumn(ICoverageNode.CounterEntity.BRANCH, locale),
        //				false);
        //		t.add("Cov.", Styles.CTR2, new PercentageColumn(ICoverageNode.CounterEntity.BRANCH, locale),
        //				false);
        //		addMissedTotalColumns(t, "Cxty", ICoverageNode.CounterEntity.COMPLEXITY);

        t.add("Missed Lines", Styles.BAR, new BarColumn(ICoverageNode.CounterEntity.LINE, getLocale()), true);
        t.add("Total Lines", Styles.CTR1, CounterColumn.newTotal(ICoverageNode.CounterEntity.LINE, getLocale()), false);

		addMissedTotalColumns(t, "Methods", ICoverageNode.CounterEntity.METHOD);
		addMissedTotalColumns(t, "Classes", ICoverageNode.CounterEntity.CLASS);
		return t;
	}

	private void addMissedTotalColumns(final Table table, final String label,
			final ICoverageNode.CounterEntity entity) {
		table.add("Missed", Styles.CTR1,
				CounterColumn.newMissed(entity, getLocale()), false);
		table.add(label, Styles.CTR2, CounterColumn.newTotal(entity, getLocale()),
				false);
	}
}
