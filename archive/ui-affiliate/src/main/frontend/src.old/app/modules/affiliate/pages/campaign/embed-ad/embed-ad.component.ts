import { Component, OnInit } from '@angular/core';
import { MdDialogRef, MdSnackBar } from '@angular/material';
import { CampaignAd } from '../../../classes/campaignAd';
import * as _ from 'lodash';
import * as Clipboard from 'clipboard';

@Component({
  selector: 'app-embed-ad',
  templateUrl: './embed-ad.component.html',
  styleUrls: ['./embed-ad.component.scss']
})
export class EmbedAdComponent implements OnInit {

  constructor(private dialogRef: MdDialogRef<EmbedAdComponent>, private snackbar: MdSnackBar) { }

  private ca: CampaignAd;
  private code: string;

  ngOnInit() {
    this.ca = this.dialogRef.config.data;
    this.code = _.escape(
`<iframe src="http://localhost:9000/service-affiliate/servesmc">
  Something here... Lorem ipsum dolor sit amet, consectetur adipiscing elit. Cras pulvinar venenatis blandit. Quisque ut tellus diam. Donec sed lacus ante. Sed eget cursus orci, non mollis odio. Nullam id mattis enim. Nulla nibh odio, elementum in pharetra vitae, semper sed odio. Fusce vel risus arcu. Ut vehicula sagittis ligula. Nulla a lorem placerat, malesuada elit luctus, sollicitudin neque. Duis id orci ante. Fusce efficitur ex commodo, tincidunt est ac, interdum sapien. Fusce finibus augue enim, non pharetra metus imperdiet in.
</iframe>`
    );
    let c = new Clipboard("#embed-ad-copy-button");
    c.on("error", (e: Clipboard.Event) => console.log(e));;
    c.on("success", (e: Clipboard.Event) => {
      this.snackbar.open("Copied to clipboard", "Close", { duration: 2000 });
    });
  }

  copy(elem: any) {
  }


}
