import { Component, OnInit } from '@angular/core';
import { MdDialogRef } from '@angular/material';

@Component({
  selector: 'app-loading-dialog',
  templateUrl: './loading-dialog.component.html',
  styleUrls: ['./loading-dialog.component.scss']
})
export class LoadingDialogComponent implements OnInit {

  private text: string = 'Loading...';

  constructor(public dialogRef: MdDialogRef<LoadingDialogComponent>) { }

  ngOnInit() {
//TODO???    if (this.dialogRef.config.data.text) this.text = this.dialogRef.config.data.text;
  }

}
