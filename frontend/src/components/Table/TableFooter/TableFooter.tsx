import React from "react";

import styles from "./TableFooter.module.css";

const TableFooter = (props: { allPagesCount: number, setPage: Function, page: number }) => {
  const range = Array.from(Array(props.allPagesCount).keys()).map(value => value + 1);
  return (
      <div className={styles.tableFooter}>
        {range.map((el, index) => (
            <button
                key={index}
                className={`${styles.button} ${
                    props.page === el ? styles.activeButton : styles.inactiveButton
                }`}
                onClick={() => props.setPage(el)}
            >
              {el}
            </button>
        ))}
        <div>Page {props.page} of {props.allPagesCount}</div>
      </div>
  );
};

export default TableFooter;