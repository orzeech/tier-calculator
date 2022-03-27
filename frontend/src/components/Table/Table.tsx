import React from "react";

import styles from "./Table.module.css";
import TableFooter from "./TableFooter/TableFooter";

type TableParams = {
  data: any[],
  page: number,
  setPage: Function,
  rowsPerPage: number,
  allPagesCount: number,
  headerTexts: string[],
  rowTexts: Function[]
};
const Table = (props: TableParams) => {
  return (
      <div className={styles.tableContainer}>
        <table className={styles.table}>
          <thead className={styles.tableRowHeader}>
          <tr>
            {props.headerTexts.map(header =>
                <th className={styles.tableHeader}>{header}</th>
            )}

          </tr>
          </thead>
          <tbody>
          {props.data.map((el) => (
              <tr className={styles.tableRowItems}>
                {props.rowTexts.map(rowTextExtractor => (
                    <td className={styles.tableCell}>{rowTextExtractor(el)}</td>
                ))}
              </tr>
          ))}
          </tbody>
        </table>
        <TableFooter allPagesCount={props.allPagesCount} setPage={props.setPage} page={props.page}/>
      </div>
  );
};

export default Table;