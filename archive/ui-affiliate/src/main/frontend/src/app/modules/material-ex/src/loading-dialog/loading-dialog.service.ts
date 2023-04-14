import { Injectable } from '@angular/core';
import { MdDialog, MdDialogRef } from '@angular/material';
import { LoadingDialogComponent } from './loading-dialog.component';
import { Observable } from 'rxjs/Observable';

export interface LoadingDialogConfig {
  text?: string;
  timeout?: number;
}

export class LoadingDialog {
  constructor(public service: LoadingDialogService, public config: LoadingDialogConfig) {}

  private ref: MdDialogRef<LoadingDialogComponent>;

  public present() {
    if (this.ref) this.ref.close();
    this.ref = this.service.dialog.open(LoadingDialogComponent, 
      { 
        disableClose: true,
        data: this.config
      }
    );
    if (this.config.timeout) {
      setTimeout( () => { this.dismiss(); }, this.config.timeout);
    }
  }

  public dismiss() {
    if (this.ref) 
    this.ref.close();
    this.ref = null;
  }

}

@Injectable()
export class LoadingDialogService {

  constructor(public dialog: MdDialog) { }

  public create(config: LoadingDialogConfig): LoadingDialog {
    return new LoadingDialog(this, config);
  }

}
